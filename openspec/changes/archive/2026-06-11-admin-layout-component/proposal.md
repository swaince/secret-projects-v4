## Why

项目前端处于脚手架初始状态，无布局组件、无路由配置、无页面结构。为支撑后续后台管理功能开发（用户管理、角色管理、系统设置等），需先建立通用后台管理布局组件作为所有管理页面的载体。当前无任何基础设施可复用，是建立布局框架的最佳时机。

## What Changes

**设计系统加载链**
- From: `src/styles/index.css` 仅 `@import "tailwind.css";`
- To: 追加 `@import "design-system.css";`，使其 CSS 变量覆盖 tailwind.css 默认值
- Reason: `frontend/AGENTS.md` 声明 `design-system.css` 具有最终决定权，但未被引入
- Impact: 非破坏性 — 全局样式变量变更（中性灰 → 靛蓝紫色调，圆角 0.625rem → 1.4rem），现有组件自动适配

**路由和页面渲染**
- From: `App.vue` 仅显示静态占位文字 "You did it!"，`router/index.ts` routes 为空数组
- To: `App.vue` 包含 `<RouterView />`；路由由 `createRoutesFromMenu(menuConfig)` 自动生成
- Reason: 布局组件需要路由系统驱动页面切换
- Impact: 非破坏性 — 现有 E2E 测试（检查 "You did it!"）需同步更新

**新增布局组件体系**
- 新增 `AdminLayout.vue`、`AppHeader.vue`、`AppSidebar.vue`、`AppBreadcrumb.vue`
- 新增 `config/menu.ts`（MenuItem 类型定义 + 菜单配置）
- 新增 `composables/useMenuNavigation.ts`、`composables/useBreadcrumb.ts`
- 新增 `stores/theme.ts`（Pinia 主题管理）
- 安装 8 个 shadcn-vue 组件：sidebar、button、dropdown-menu、badge、avatar、tooltip、breadcrumb、separator
- 创建目录：`src/layouts/`、`src/components/layout/`、`src/config/`、`src/composables/`

**测试体系**
- 新增 8 个测试文件，覆盖纯函数 → composables → stores → 组件（按 TDD 顺序）

## Capabilities

### New Capabilities
- `admin-layout-shell`: 通用后台管理布局壳组件，集成顶部通栏、可折叠侧边栏、面包屑和主内容区。支撑三层菜单结构，一级菜单在顶部导航，二/三级菜单在侧边栏手风琴展开
- `menu-config`: 静态菜单配置系统，定义 `MenuItem` 类型和树形菜单数据，支持 `createRoutesFromMenu` 纯函数自动生成 Vue Router 路由
- `theme-persistence`: 亮色/暗色主题切换，Pinia store 管理状态，localStorage 持久化，通过 `.dark` class 驱动 Tailwind v4 暗色模式

### Modified Capabilities
- 无。当前 `openspec/specs/` 为空，无已存在的 capability 需修改

## Impact

- **前端依赖**：shadcn-vue 新增 8 个组件安装（sidebar、button、dropdown-menu、badge、avatar、tooltip、breadcrumb、separator），无新增 npm 包（均为 shadcn-vue 内置注册组件）
- **样式系统**：`index.css` 追加 `design-system.css` 导入，全局 CSS 变量变更（中性灰 → 紫色调），影响所有后续页面渲染
- **路由系统**：`router/index.ts` 增加路由自动生成逻辑，`App.vue` 增加 `<RouterView />`，影响 E2E 测试
- **目录结构**：新增 4 个目录（layouts/、components/layout/、config/、composables/）
- **后端**：无影响
