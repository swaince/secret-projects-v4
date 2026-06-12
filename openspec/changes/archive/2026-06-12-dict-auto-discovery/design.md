## Context

项目存在双字典体系：

1. **枚举静态字典**：通过 `@Dictionary` 注解 + `DictionaryElement<T>` 接口定义，位于 `common/constants/` 下，目前包含 5 个枚举（BizCode、Status、DataValueType、Builtin、Deleted）。
2. **数据库动态字典**：`sys_dict` + `sys_dict_item` 表，拥有完整 CRUD 及 REST API（`/dicts`）。

**核心问题**：两套体系之间缺少自动同步桥梁。枚举字典无法通过统一 API 暴露给前端，前端若要使用枚举定义的字典值，只能硬编码或走额外接口。

**约束条件**：
- Spring Boot 启动生命周期内完成同步
- 不改动已有注解/接口定义
- 不影响业务动态字典（`built_in=0`）
- Flyway 管理数据库迁移，需兼容 H2 测试环境

## Goals / Non-Goals

**Goals:**
- 应用启动时自动扫描所有 `@Dictionary` 枚举，全量同步至数据库，标记为内置字典（`built_in=1`）
- 代码枚举作为内置字典的唯一真相来源（Single Source of Truth）
- 同步后内置字典可通过现有 `/dicts` API 统一查询，前端无需区分字典来源
- 新增 `item_label` 列，将枚举的 `getMessage()` 显示文本持久化到数据库

**Non-Goals:**
- 不新增 API 端点 — 复用现有 `/dicts` 即可
- 不修改前端代码 — 前端已对接 `/dicts`，同步透明
- 不修改 `@Dictionary` 注解或 `DictionaryElement` 接口定义
- 不涉及 Service/Controller 层变更

## Decisions

### D1: 全量同步策略（Delete-then-Insert）
- **选择**: 每次启动时删除所有 `built_in=1` 的字典及字典项，从 `@Dictionary` 枚举重新全量插入
- **理由**: 内置字典的定义权在代码中，管理员不应修改内置字典。全量同步确保数据库始终与代码一致，逻辑简单、无需 diff 比较。业务动态字典（`built_in=0`）不受影响
- **已考虑替代方案**:
  - **仅新增不覆盖**：枚举不存在时插入，已存在跳过。问题是枚举修改后数据库无法感知，导致数据不一致
  - **Upsert 增量同步**：需要逐条比对、处理删除的枚举元素，复杂度高但收益不明显
  - **仅内存不入库**：无法通过统一 API 暴露，违背设计初衷

### D2: 轻量 ApplicationRunner + 直接 Mapper
- **选择**: 新建 `DictSyncRunner implements ApplicationRunner`，直接注入 `DictMapper` / `DictItemMapper` 完成数据操作
- **理由**: 枚举数量少（目前 5 个），启动扫描开销可忽略。一个类完成全部逻辑，符合 YAGNI 原则。内置字典的生命周期（启动时全量刷新）与业务字典（用户按需 CRUD）本质不同，绕过 Service 层合理
- **已考虑替代方案**:
  - **Service 层抽象**：关注点分离更好，但对一个仅在启动时运行的同步逻辑而言是过度抽象
  - **编译期注解处理器**：零运行时扫描开销，但构建复杂度高、调试困难，枚举数量少时收益极低

### D3: 全基础包扫描
- **选择**: 扫描范围为 `com.dfec.soft.secret` 整个基础包
- **理由**: 未来业务模块也可以定义自己的 `@Dictionary` 枚举，全包扫描确保自动发现。通过 `ClassPathScanningCandidateComponentProvider` + `AnnotationTypeFilter(@Dictionary)` 实现，并过滤仅保留实现 `DictionaryElement` 的枚举类
- **已考虑替代方案**:
  - **仅扫描 `common.constants`**：限制过严，未来在其他模块新增枚举时需手动修改扫描路径

### D4: 同步失败时拒绝启动（Fail-Fast）
- **选择**: 同步过程中任何异常导致事务回滚并阻止应用启动
- **理由**: 字典数据是基础数据，若同步失败则运行时可能出现数据不一致。在启动阶段暴露问题比运行时静默失败更安全
- **已考虑替代方案**:
  - **警告但继续启动**：可能导致前端查询内置字典时返回空或过期数据，问题难以排查

### D5: 新增 `item_label` 列
- **选择**: 通过 Flyway 迁移脚本为 `sys_dict_item` 表新增 `item_label VARCHAR(100)` 列，映射枚举的 `getMessage()` 显示文本
- **理由**: 现有表结构缺少承载枚举显示文本的字段。`item_key` 存储枚举名（`getCode()`），`item_value` 存储枚举值（`getValue()`），`item_label` 存储人类可读标签（`getMessage()`），三者职责清晰
- **已考虑替代方案**:
  - **复用 `item_value` 或 `item_key`**：语义混乱，破坏已有数据模型

## Risks / Trade-offs

- **[Trade-off] 绕过 Service 层** → 接受理由：内置字典的生命周期（启动时全量刷新）与业务字典（用户 CRUD）本质不同，直接操作 Mapper 更简单直接。若未来同步逻辑变复杂，可重构为 Service
- **[Risk] 启动时间随枚举数量增长** → Mitigation：当前仅 5 个枚举，扫描+插入耗时可忽略。即使增长到数十个，ClassPath 扫描 + 批量 INSERT 仍在毫秒级。若极端情况可改为异步或缓存扫描结果
- **[Risk] Delete-then-Insert 存在短暂数据空窗期** → Mitigation：整个同步操作在 `@Transactional` 事务中执行，外部不可见中间状态。且同步发生在 ApplicationRunner 阶段，此时应用尚未接受请求
- **[Risk] Flyway 迁移失败** → Mitigation：`V1.0.0.2__add_dict_item_label.sql` 使用 `ADD COLUMN` 语法，H2 和 PostgreSQL 均兼容。DDL 简单，失败概率极低

## Migration Plan

**部署顺序：**
1. **Flyway 迁移**：`V1.0.0.2__add_dict_item_label.sql` 在应用启动时自动执行，为 `sys_dict_item` 新增 `item_label` 列
2. **实体/DTO 适配**：`SysDictItem` 和 `DictItemDTO` 新增 `itemLabel` 字段，MapStruct 同名自动映射无需额外配置
3. **同步执行**：`DictSyncRunner` 在 Flyway 迁移完成后运行，扫描枚举并全量同步内置字典

**回滚策略：**
- 移除 `DictSyncRunner` 类即可停止自动同步。已写入的 `built_in=1` 数据可通过 SQL 手动清理
- `item_label` 列为新增列且有默认值，不影响现有功能，无需回滚

**验收标准：**
- 启动后 `sys_dict` 中存在所有 `@Dictionary` 枚举对应的记录，`built_in=1`
- 枚举元素与 `sys_dict_item` 记录一一对应，`item_label` 正确填充
- 重复启动后数据幂等
- 新增/修改/删除枚举元素后重启，数据库同步更新
- 业务动态字典（`built_in=0`）不受影响
- 通过 `/dicts` API 可查询到内置字典

## Open Questions

无 — 所有设计决策已在头脑风暴阶段确定。
