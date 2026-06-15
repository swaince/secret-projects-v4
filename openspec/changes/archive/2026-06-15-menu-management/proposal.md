## Why

当前前端导航菜单硬编码在 `src/config/menu.ts`，无法通过后台管理。新增页面需要改代码、重新部署。需要数据库驱动的菜单管理，支持无限层级和按钮权限标识，为后续权限系统打基础。

## What Changes

**后端：新增菜单 CRUD + 树查询接口**
- From: 无菜单管理后端
- To: sys_menu 表 + 树形 CRUD REST API + MenuType 枚举
- Reason: 动态管理导航结构
- Impact: non-breaking，纯新增

**前端：新增菜单管理页面**
- From: 无菜单管理入口
- To: 树形表格管理页面（增删改查、类型/图标/路径配置）
- Reason: 提供可视化菜单配置能力
- Impact: non-breaking，新增页面

**前端：动态菜单替换静态配置**
- From: `src/config/menu.ts` 硬编码菜单
- To: Pinia store 从接口加载菜单树 → 动态注册路由
- Reason: 实现菜单数据库驱动
- Impact: breaking（删除 menu.ts），需迁移现有数据

## Capabilities

### New Capabilities

- `menu-crud`: 菜单管理 CRUD — 后端树形接口 + 前端管理页面 + 动态路由加载

### Modified Capabilities

- `menu-config`: 现有静态菜单配置将被数据库驱动的动态菜单替代

## Impact

- **后端新增**：SysMenu 实体、MenuDTO、MenuMapper、MenuStructMapper、MenuService/Impl、MenuController、MenuType 枚举、Flyway 建表 + 数据迁移
- **前端新增**：`src/api/menu.ts`、`src/views/system/MenuView.vue`、`src/stores/menu.ts`（动态菜单 store）
- **前端修改**：`src/router/index.ts`（动态路由注册）、`src/App.vue` 或布局组件（从 store 读取菜单）
- **前端删除**：`src/config/menu.ts`（迁移后废弃）
- **依赖**：无新外部依赖
- **测试**：后端 H2 集成测试
