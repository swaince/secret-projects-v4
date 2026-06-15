## Context

前端菜单目前硬编码在 `src/config/menu.ts`，无法通过后台动态管理。需要实现数据库驱动的菜单体系，支持无限层级树、页面按钮权限标识，并替换前端静态菜单为接口动态加载。

约束：
- 后端：Spring Boot 3.5 + MyBatis-Plus + MapStruct + Flyway
- 前端：Vue 3.5 + vue-router + Pinia + shadcn-vue
- 树结构用 parentId 自引用实现，不限层级
- 按钮类型仅记录权限标识，不生成路由

## Goals / Non-Goals

**Goals:**
- 后端：sys_menu 表 + 树形 CRUD 接口（增删改查 + 树查询）
- 前端：菜单管理页面（树形表格 CRUD）
- 前端：动态菜单加载（替换 menu.ts，从接口获取菜单树 → 动态注册路由）
- 支持目录(D)/菜单(M)/按钮(B) 三种类型
- 支持重定向、是否需要认证配置

**Non-Goals:**
- 不做按钮权限的运行时判断（v-permission 指令后续单独做）
- 不做拖拽排序（通过 sortOrder 字段手动设置）
- 不做角色-菜单绑定（后续权限变更）

## Decisions

### D1：树结构存储

- **选择**：parentId 自引用（adjacency list）
- **理由**：简单直观，MyBatis-Plus 原生支持，查询后在 Service 层组装树
- **已考虑 alternative**：嵌套集模型 — 更新成本高 → 拒绝；path 枚举 — 查询方便但维护复杂 → 拒绝

### D2：后端树接口

- **选择**：`GET /menus/tree` 返回完整菜单树（已组装），`GET /menus` 返回平铺列表
- **理由**：树接口供前端渲染导航用，平铺列表供管理页面编辑用

### D3：前端动态路由

- **选择**：登录后请求菜单树 → 遍历生成 RouteRecordRaw → `router.addRoute()` 动态注册
- **理由**：Vue Router 4 原生支持动态路由注册
- **component 字段**：存储相对路径如 `system/UserView`，前端用 `import()` 动态加载

### D4：menuType 枚举

- **选择**：后端 `@Dictionary` 枚举 `MenuType`（D=目录, M=菜单, B=按钮）
- **理由**：与项目字典体系一致，前端下拉框走 dict 翻译

### D5：前端管理页面

- **选择**：树形表格（参考 DeptView.vue 的 flatNodes 模式）
- **理由**：无限层级用 Table + 缩进展示，与现有部门管理页面模式一致

## Risks / Trade-offs

[Risk] 动态路由注册时组件路径错误会导致白屏 → Mitigation: component 字段校验 + 404 兜底路由

[Trade-off] 全量加载菜单树（无分页）→ 接受：菜单数据量有限（通常 <200 条），不需要分页

[Trade-off] 替换 menu.ts 后原有静态路由失效 → Mitigation: 保留基础路由（登录/404），仅业务路由动态化

## Migration Plan

1. Flyway 创建 sys_menu 表
2. 后端 Entity → DTO → Mapper → Service → Controller
3. 后端 MenuType 枚举（@Dictionary）
4. 前端菜单管理页面（树形 CRUD）
5. 前端 menuStore + 动态路由注册
6. 迁移 menu.ts 现有数据到数据库（Flyway 数据迁移脚本）
7. 删除 menu.ts 硬编码

## Open Questions

- 是否需要 Flyway 脚本初始化现有菜单数据？（建议是，确保升级平滑）
