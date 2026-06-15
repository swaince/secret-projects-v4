## Why

系统管理模块缺少用户管理功能。用户是系统的核心实体，后续的权限、审计、操作日志等功能都依赖用户数据。需要一个完整的用户 CRUD 模块作为基础设施。

## What Changes

**后端：新增用户 CRUD 接口**
- From: 无用户管理功能
- To: 完整的用户增删改查 REST API（分页查询、创建、修改、删除、批量删除、状态切换）
- Reason: 系统核心基础模块
- Impact: non-breaking，纯新增

**前端：新增用户管理页面**
- From: 系统管理下无用户管理入口
- To: UserView.vue 管理页面（表格、搜索、对话框 CRUD、状态切换、字典翻译）
- Reason: 提供可视化用户管理能力
- Impact: non-breaking，新增页面和菜单项

## Capabilities

### New Capabilities

- `user-crud`: 用户管理 CRUD — 包含后端实体/DTO/Mapper/Service/Controller、Flyway 建表、前端 API 层和管理页面

### Modified Capabilities

无

## Impact

- **后端新增文件**：SysUser 实体、UserDTO、UserPageRequest、UserMapper、UserStructMapper、UserService/Impl、UserController、Flyway 迁移脚本、集成测试
- **前端新增文件**：`src/api/user.ts`、`src/views/system/UserView.vue`
- **前端修改文件**：`src/config/menu.ts`（新增菜单项）、`src/dict/index.ts`（新增 gender 注册）
- **依赖**：可能需要 spring-security-crypto（BCryptPasswordEncoder）
- **测试**：后端 H2 集成测试 + 前端无额外测试（页面级）
