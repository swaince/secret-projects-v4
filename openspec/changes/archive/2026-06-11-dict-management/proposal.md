## Why

后台管理系统需要字典管理作为基础设施，支撑所有枚举、下拉选项、状态值的统一维护。当前 `springboot-web-dev` 技能已定义字典模块完整规范（DictionaryElement 接口、@Dictionary 注解、内置枚举 Status/Builtin/Deleted/DataType），但实际 CRUD 代码和前端页面尚未实现。字典管理是所有业务模块的前置依赖，必须在业务功能开发前落地。

## What Changes

**后端新增字典管理模块**

在 `common/` 包下新增字典和字典项完整 CRUD：
- `SysDict` 实体 + `SysDictItem` 实体（双表，dict_id 外键关联）
- `DictController`（分页列表、新增、修改、删除、批量删除）
- `DictItemController`（新增、修改、删除、批量删除、按字典ID批量删除、按字典编码批量删除、查询字典项列表）
- `DictService` + `DictItemService`（遵循 springboot-web-dev 全部规范）
- Flyway 迁移脚本 `V1.0.0.0__init_data.sql`（sys_dict + sys_dict_item DDL + COMMENT ON）

**前端新增字典管理页面**

- 字典分页列表页面（`views/system/dict/Index.vue`）
- 字典项右侧抽屉组件（`DictItemDrawer.vue`）
- 字典项支持状态（启用/禁用）、内置标记、软删除
- 使用 shadcn-vue Drawer/Table/Button/Input/Dialog 组件

**测试体系**

- 后端：DictServiceTest、DictItemServiceTest（TDD 先行，H2 + AssertJ）
- 前端：字典页面测试、字典项抽屉测试

## Capabilities

### New Capabilities
- `dict-crud`: 字典管理后端 CRUD 接口。字典（新增/修改/删除/批量删除/分页列表）+ 字典项（新增/修改/删除/批量删除/按字典ID删除/按字典编码删除/查询列表）。字典创建时指定 data_value_type 限定项值类型。
- `dict-ui`: 字典管理前端页面。字典分页列表 + 右侧抽屉展示字典项（不分页，批量删除，数值编辑排序）。字典项支持 status/built_in/deleted 字段管理。

### Modified Capabilities
- 无。当前 `openspec/specs/` 为空，无已存在的 capability 需修改。

## Impact

- **后端**：新增 2 个 Controller、2 个 Service、2 个 Mapper、2 个 Entity、4 个 DTO、2 个 MapStruct、1 个 Flyway 脚本
- **前端**：新增 2 个 Vue 组件、1 条路由
- **数据库**：新增 sys_dict + sys_dict_item 表（含审计字段 + COMMENT ON）
- **测试**：后端 2 个测试类（TDD 先行），前端 2 个测试文件
- **前端依赖**：shadcn-vue Drawer 组件（已安装）、Table/Button/Input/Dialog（已安装）
