# 字典自动发现及自动注册 — Brainstorm

## 背景

项目存在双字典体系：

1. **枚举静态字典**：`@Dictionary` 注解 + `DictionaryElement<T>` 接口，定义在 `common/constants/` 下，目前有 5 个枚举（BizCode, Status, DataValueType, Builtin, Deleted）
2. **数据库动态字典**：`sys_dict` + `sys_dict_item` 表，完整 CRUD + REST API (`/dicts`)

**缺失环节**：没有机制在应用启动时扫描 `@Dictionary` 枚举并同步到数据库，导致枚举字典无法通过统一 API 暴露给前端。

## 决策链

### Q1: 注册策略

- **选项 A**: 仅新增不覆盖 — 枚举不存在时插入，已存在跳过
- **选项 B**: 完全同步（Upsert）— 每次启动以代码为准覆盖
- **选项 C**: 仅内存，不入库

**决定**: **全量同步** — 启动时删除所有 `built_in=1` 的字典及字典项，从 `@Dictionary` 枚举重新全量插入。代码枚举是内置字典的唯一真相来源。业务动态字典（`built_in=0`）不受影响。

**理由**: 内置字典的定义权在代码中，管理员不应修改内置字典。全量同步确保 DB 始终与代码一致，避免增量同步的复杂性。

### Q2: 新增 Item 策略

随 Q1 一并解决 — 全量同步天然覆盖新增/删除/修改的枚举元素。

### Q3: API 需求

- **选项 A**: 无需额外接口，复用现有 `/dicts`
- **选项 B**: 新增专用接口

**决定**: **无需额外接口**。内置字典写入 DB 后，通过现有 `/dicts` API 即可查询，前端无需区分来源。

### Q4: 扫描范围

- **选项 A**: 全基础包 `com.dfec.soft.secret`
- **选项 B**: 仅 `common.constants`

**决定**: **全基础包扫描**。未来业务模块也可以定义自己的 `@Dictionary` 枚举，自动被发现注册。

### Q5: 失败策略

- **选项 A**: 拒绝启动
- **选项 B**: 警告但继续

**决定**: **拒绝启动**。字典同步失败时抛异常，阻止应用启动，确保字典数据始终可用。

## 设计取舍

### 方案对比

| 方案 | 描述 | 优点 | 缺点 |
|------|------|------|------|
| **A（采用）** | 轻量 ApplicationRunner + 直接 Mapper | 简单直接，一个类搞定 | 绕过 Service 层 |
| B | Service 层抽象 | 关注点分离好 | 过度抽象 |
| C | 编译期注解处理器 | 零运行时扫描 | 构建复杂度高，收益低 |

**选择方案 A** — 枚举数量少（目前 5 个），启动扫描开销可忽略。一个类完成全部逻辑，符合 YAGNI 原则。绕过 Service 层合理，因为内置字典的生命周期不同于业务字典。

## 组件结构

**新增文件：** `common/config/DictSyncRunner.java`

```
DictSyncRunner implements ApplicationRunner
├── 注入: DictMapper, DictItemMapper, UidService
├── run(ApplicationArguments)
│   └── syncBuiltinDicts()   // @Transactional
│       ├── scanDictionaryEnums()     // ClassPath 扫描
│       ├── deleteBuiltinDicts()      // DELETE WHERE built_in = 1
│       └── insertBuiltinDicts()      // 批量插入 dict + items
```

## 字段映射

### sys_dict

| 枚举源 | DB 字段 |
|--------|---------|
| `@Dictionary.name()` | `dict_name` |
| `@Dictionary.code()` | `dict_code` |
| `getValue()` 类型推断 | `data_value_type` |
| 固定 `Builtin.YES` | `built_in` |
| 固定 `Status.ENABLED` | `status` |
| 固定 `Deleted.NO` | `deleted` |

### sys_dict_item

| 枚举源 | DB 字段 |
|--------|---------|
| `getCode()` (enum name) | `item_key` |
| `getMessage()` (显示文本) | `item_label`（新增列） |
| `String.valueOf(getValue())` | `item_value` |
| `ordinal()` | `sort_order` |
| 固定 `Builtin.YES` | `built_in` |
| 固定 `Status.ENABLED` | `status` |
| 固定 `Deleted.NO` | `deleted` |

## 执行流程

```
Spring Context Ready
  └─ ApplicationRunner.run()
       └─ DictSyncRunner.syncBuiltinDicts()  @Transactional
            ├─ 1. scanDictionaryEnums()
            │    └─ ClassPathScanningCandidateComponentProvider
            │         + AnnotationTypeFilter(@Dictionary)
            │         + basePackage = "com.dfec.soft.secret"
            │    └─ 过滤: 仅保留实现 DictionaryElement 的枚举类
            │    └─ 返回: List<Class<? extends DictionaryElement>>
            │
            ├─ 2. deleteBuiltinDicts()
            │    └─ DELETE FROM sys_dict_item WHERE built_in = 1
            │    └─ DELETE FROM sys_dict WHERE built_in = 1
            │    （先删 item 再删 dict）
            │
            └─ 3. insertBuiltinDicts(enumClasses)
                 └─ 遍历每个枚举类:
                      ├─ 读取 @Dictionary(name, code)
                      ├─ 推断 dataValueType
                      ├─ 生成 dictId (UidService.next())
                      ├─ INSERT sys_dict
                      └─ 遍历枚举常量:
                           ├─ 生成 dictItemId (UidService.next())
                           └─ INSERT sys_dict_item
```

**异常处理**：任何异常 → 事务回滚 → Spring Boot 拒绝启动。日志记录扫描开始/结束 + 发现枚举数量。

## DB 迁移

**Flyway 脚本：** `V1.0.0.2__add_dict_item_label.sql`

```sql
ALTER TABLE sys_dict_item
    ADD COLUMN item_label VARCHAR(100) DEFAULT '' NOT NULL;

COMMENT ON COLUMN sys_dict_item.item_label IS '字典项标签';
```

H2 语法兼容，测试无需特殊处理。

## 变更影响范围

**新增（2 个文件）：**
- `common/config/DictSyncRunner.java` — 启动时扫描 + 全量同步内置字典
- `db/migration/V1.0.0.2__add_dict_item_label.sql` — 新增 `item_label` 列

**修改（2 个文件）：**
- `SysDictItem.java` — 新增 `itemLabel` 字段 + getter/setter
- `DictItemDTO.java` — 新增 `itemLabel` 字段

**不修改：**
- `DictItemStructMapper.java` — 同名自动映射
- `DictService` / `DictItemService` — 不涉及
- `DictController` / `DictItemController` — 不涉及
- `@Dictionary` / `DictionaryElement` — 无需修改

**测试：**
- 新增 `DictSyncRunnerTest`：扫描发现枚举、全量同步、幂等性、新增枚举元素后同步正确
- 现有测试适配 `item_label` 新字段
