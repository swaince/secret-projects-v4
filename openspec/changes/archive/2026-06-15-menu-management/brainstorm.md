# 菜单管理 — 头脑风暴记录

## 背景

当前前端菜单硬编码在 `src/config/menu.ts`，无法动态管理。需要实现数据库驱动的菜单管理，支持无限层级树结构和页面按钮权限。

## 决议链

### Q1：实现范围？

**决定**：前后端都做。后端菜单 CRUD + 前端管理页面 + 前端动态菜单渲染（替换硬编码 menu.ts）。

### Q2：菜单类型？

**决定**：三种类型，parentId 实现无限层级。
- 目录（D）：分组容器，不对应页面
- 菜单（M）：对应实际页面，有 path + component
- 按钮（B）：页面内操作按钮，仅做权限标识

### Q3：字段设计？

**决定**：
- menuId（主键）
- parentId（父级 ID，根节点为 null 或空）
- menuName（菜单名称）
- menuType（D=目录 / M=菜单 / B=按钮）
- path（路由路径）
- component（组件路径，如 views/system/UserView）
- icon（图标名称）
- sortOrder（排序号）
- status（状态 1=启用 0=禁用）
- permission（权限标识，如 system:user:add）
- visible（是否可见 1=可见 0=隐藏）
- redirect（重定向路径）
- requireAuth（是否需要认证 1=需要 0=不需要）
- 审计字段（createAt, createBy, updateAt, updateBy, deleted）

### Q4：前端动态菜单如何替换？

**决定**：
- 新增 API `GET /menus/tree` 返回当前用户可见的菜单树
- 前端启动时请求菜单树，动态注册路由
- `src/config/menu.ts` 废弃，改为从 store 读取菜单数据
- 菜单管理页面用树形表格展示，支持拖拽排序（可选）

### Q5：按钮权限如何使用？

**决定**：
- 按钮类型菜单不渲染为导航项，仅记录 permission 标识
- 后续通过 `v-permission` 指令或 `hasPermission()` 工具函数控制按钮可见性
- 本次变更仅做数据录入和存储，权限判断逻辑后续变更实现
