## Why

项目存在双字典体系：枚举静态字典（`@Dictionary` + `DictionaryElement`）和数据库动态字典（`sys_dict` + `sys_dict_item`），但两者之间没有桥梁——枚举定义的内置字典无法通过统一的 `/dicts` API 暴露给前端。字典 CRUD 后端与前端 UI 均已就绪，现在是补齐"枚举→DB"同步环节的最佳时机。完成后，前端只需调用一个 API 即可获取所有字典（内置+业务），代码枚举成为内置字典的唯一真相来源。

## What Changes

| 维度 | From | To |
|------|------|----|
| 内置字典可见性 | 仅存在于 Java 枚举中，前端无法通过 API 获取 | 启动时自动同步到 DB，通过现有 `/dicts` API 可查询 |
| 启动行为 | 无字典同步逻辑 | `DictSyncRunner`（ApplicationRunner）全量同步：删除所有 `built_in=1` 的字典及字典项，从 `@Dictionary` 枚举重新插入 |
| `sys_dict_item` 表结构 | 无 `item_label` 列 | 新增 `item_label VARCHAR(100)` 列，存储枚举 `getMessage()` 返回的显示文本 |
| `SysDictItem` 实体 | 无 `itemLabel` 字段 | 新增 `itemLabel` 字段 |
| `DictItemDTO` | 无 `itemLabel` 字段 | 新增 `itemLabel` 字段 |
| 同步失败处理 | 不适用 | 抛异常，阻止应用启动，确保字典数据完整性 |

## Capabilities

### New Capabilities

- **dict-auto-sync** — 应用启动时自动扫描 `@Dictionary` 枚举，全量同步内置字典到数据库。扫描范围为基础包 `com.dfec.soft.secret`，同步策略为先删后插（以代码为准），失败时拒绝启动。

### Modified Capabilities

- **dict-crud** — `sys_dict_item` 表新增 `item_label` 列，`SysDictItem` 实体和 `DictItemDTO` 新增 `itemLabel` 字段。现有字典项 CRUD 接口的请求/响应结构将包含该新字段。

## Impact

**新增文件：**
- `backend/.../common/config/DictSyncRunner.java` — 启动同步逻辑
- `backend/.../db/migration/V1.0.0.2__add_dict_item_label.sql` — Flyway 迁移脚本

**修改文件：**
- `SysDictItem.java` — 新增 `itemLabel` 字段
- `DictItemDTO.java` — 新增 `itemLabel` 字段

**数据库：**
- `sys_dict_item` 表新增 `item_label` 列（VARCHAR(100)，非空，默认空字符串）

**运行时行为：**
- 每次应用启动时执行内置字典全量同步，增加少量启动时间（目前 5 个枚举，可忽略）
- 同步在事务中执行，失败则回滚并阻止启动

**测试：**
- 新增 `DictSyncRunnerTest`（扫描、同步、幂等性验证）
- 现有涉及 `DictItemDTO` / `SysDictItem` 的测试需适配 `itemLabel` 字段
