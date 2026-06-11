## Context

secret-projects-v4 前端处于脚手架初始状态：无布局组件、无页面路由、无组件目录、无 composables。项目使用 Vue 3.5 + Vite 8 + TypeScript 6 + Tailwind CSS v4 + shadcn-vue + pnpm，设计系统由 `design-system.css` 最终裁定（靛蓝紫色调，当前未被 `index.css` 引入）。

本次变更实现通用后台管理布局组件，支撑三层菜单结构（部分节点仅两层），包含顶部通栏（logo + 一级菜单 + 右侧操作区）、左侧可折叠侧边栏（二/三级菜单，手风琴展开）、面包屑导航和主题切换功能。

约束：
- 必须使用 shadcn-vue 组件（`frontend/AGENTS.md` 硬性规定），禁止裸写 UI
- `design-system.css` 作为设计系统具有最终决定权，组件不硬编码样式值
- Tailwind CSS v4 CSS-first 配置，无 `tailwind.config.js`
- 强制 TDD（根 `AGENTS.md`），先写测试再写实现
- pnpm 包管理器，锁文件 `pnpm-lock.yaml`
- vue-tsc 类型检查（非 tsc）
- Prettier 无分号、单引号、printWidth 100

## Goals / Non-Goals

**Goals:**
- 提供通用后台管理布局壳（AdminLayout），集成顶部通栏、左侧侧边栏、面包屑和主内容区
- 支持静态 MenuItem 类型定义和树形菜单配置，自动生成 Vue Router 路由
- 支持三层菜单（一级在顶部、二/三级在侧边栏），手风琴式展开
- 侧边栏可折叠为图标模式（64px）/ 全宽模式（240px）
- 顶部栏右区含消息通知入口、主题切换（亮/暗）、用户头像下拉菜单
- 面包屑基于当前路由 + 菜单配置自动生成路径链
- 主题状态 Pinia store 持久化至 localStorage，操作 `document.documentElement.classList`
- 修复 `index.css` 加载链，追加 `design-system.css` 覆盖默认变量

**Non-Goals:**
- 不实现移动端响应式适配（仅桌面端）
- 不实现后端 API 动态菜单（当前阶段为静态配置，数据源可替换预留）
- 不实现用户认证/权限控制
- 不实现通知数据对接后端（仅预留展示接口）
- 不实现侧边栏拖拽调整宽度

## Decisions

### D1：布局结构 — 单壳组件 + 菜单配置驱动

- **选择**：单个 `AdminLayout.vue` 作为布局壳，内嵌 `AppHeader`、`AppSidebar`、`AppBreadcrumb` 三个子组件，菜单由 `config/menu.ts` 静态配置驱动
- **理由**：结构清晰，一个入口组件管理所有布局状态；菜单配置即文档；后期切换动态数据只需替换数据源（`createRoutesFromMenu` 接受通用 `MenuItem[]`）
- **已考虑 alternative**：多组件组合 + Composable 驱动（过度抽象）；纯 shadcn-vue Sidebar 区块模式（sidebar 组件设计为全高侧边栏，与顶部通栏共存需额外适配）

### D2：菜单数据 — 静态配置 + 类型化

- **选择**：TypeScript `MenuItem` 接口定义类型，`config/menu.ts` 导出 `menuConfig: MenuItem[]`，`createRoutesFromMenu()` 纯函数递归生成路由
- **理由**：类型安全、IDE 智能提示、编译期校验；后期替换为后端数据时接口签名不变
- **已考虑 alternative**：路由 meta 直接写死（无中心化菜单配置，面包屑难以追溯）

### D3：侧边栏 — shadcn-vue Sidebar 区块

- **选择**：使用 shadcn-vue 的 `SidebarProvider` / `Sidebar` / `SidebarContent` / `SidebarMenu` 等区块组件实现侧边栏
- **理由**：符合 `frontend/AGENTS.md`"必须使用 shadcn-vue"规定；内置折叠行为、主题变量（`--sidebar-*` 已在 `tailwind.css` / `design-system.css` 预定义）
- **已考虑 alternative**：纯手写侧边栏（违反 AGENTS.md，且需要复现折叠/动画/手风琴逻辑）

### D4：一级菜单行为 — 导航式切换

- **选择**：点击一级菜单直接路由跳转到模块首页，侧边栏根据当前路由自动展示对应子菜单
- **理由**：用户符合"点击即导航"的直觉，侧边栏通过路由路径反查菜单树确定展开状态
- **已考虑 alternative**：切换式（点击一级仅切换侧边栏内容不跳转）——与用户预期不符

### D5：主题切换 — Pinia Store + localStorage

- **选择**：`useThemeStore`（Setup Store 语法），state 为 `'light' | 'dark'`，`toggle()` 切换并同步 localStorage key `app-theme`，`AdminLayout` 中 watch 操作 `document.documentElement.classList`
- **理由**：符合 Pinia Composition API 风格（`frontend/AGENTS.md` 指定 Setup Store）；Tailwind v4 暗色模式基于 `.dark` class（`@custom-variant dark (&:is(.dark *))`）
- **已考虑 alternative**：useDarkMode composable 直接操作 DOM（不便跨组件共享状态）；三态切换（过复杂，当前无系统跟随需求）

### D6：面包屑 — route.matched + 菜单树追溯

- **选择**：`useBreadcrumb` composable 结合 `route.matched` 和 `menuConfig` 树查找完整路径链，生成 `{ title, path? }[]`
- **理由**：`route.matched` 仅提供路由级信息，一级菜单节点本身非路由，需菜单树向上追溯补全；纯计算逻辑易于测试

### D7：设计系统修复 — index.css 追加 design-system.css

- **选择**：在 `src/styles/index.css` 中 `@import "tailwind.css";` 之后追加 `@import "design-system.css";`
- **理由**：`design-system.css` 被 `frontend/AGENTS.md` 声明为"具有最终决定权"但未被引入；追加在 `tailwind.css` 之后可覆盖默认 CSS 变量；`components.json` 的 `baseColor: "neutral"` 仅影响默认 shadcn 主题，design-system.css 自定义变量不受其约束

## Risks / Trade-offs

- [Risk] `AdminLayout.vue` 组件体量可能偏大（预计 150-200 行）→ Mitigation：子组件拆分已规划（AppHeader、AppSidebar、AppBreadcrumb），复杂逻辑下沉 composables，AdminLayout 仅做状态编排
- [Risk] shadcn-vue Sidebar 区块与顶部通栏组合时布局可能冲突（Sidebar 默认全高且自身含 header/footer slot）→ Mitigation：先查阅 shadcn-vue Sidebar 示例和对应 reka-ui 组件文档确认 slot 机制
- [Risk] 菜单配置单向 `createRoutesFromMenu` 后，页面组件路径约定（如 `@/views/<module>/<page>.vue`）需保持一致→ Mitigation：在 `MenuItem` meta 中预留 `component` 字段可覆盖自动路径，默认按约定拼接
- [Trade-off] 仅桌面端不支持移动端 → 接受理由：需求明确为桌面端后台系统，减少初始复杂度
- [Trade-off] 静态菜单配置需手动维护与路由同步 → 接受理由：现阶段无后端动态菜单需求，`createRoutesFromMenu` 保证菜单和路由同源，后期替换数据源即可

## Migration Plan

N/A — 本 change 为纯新增前端组件，不涉及后端部署变更、数据库迁移或 API 变更。

部署步骤：
1. `pnpm install`（shadcn-vue 新组件安装后依赖无变化，无需重装）
2. `pnpm build`（type-check + vite build）
3. 静态资源部署至 Web 服务器

回滚策略：Git revert 即可，无数据库/API 副作用。

## Open Questions

- 无。所有关键技术决策已在 brainstorm 阶段达成共识。
