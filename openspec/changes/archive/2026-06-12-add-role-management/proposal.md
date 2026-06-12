## Why

系统管理需要角色管理能力，为后续权限分配和用户-角色关联提供基础数据结构。当前菜单入口已配置但无后端/前端代码。

## What Changes

**新增后端角色管理模块**
- sys_role 表 + Flyway 迁移
- Entity / DTO / Mapper / MapStruct / Service / Controller
- REST API: `/roles` 全套 CRUD

**新增前端角色管理页面**
- `api/role.ts` + `views/system/RoleView.vue`
- menu.ts 添加 component 引用

## Capabilities

### New Capabilities
- `role-management`: 角色基础 CRUD 生命周期管理

### Modified Capabilities
（无）

## Impact

- 数据库：新增 sys_role 表
- 后端：system 包下新增一组文件
- 前端：新增 API + 页面，修改 menu.ts
- 无破坏性变更
