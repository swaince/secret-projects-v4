## 1. 基础设施搭建

- [x] 1.1 创建目录结构 `src/layouts/`、`src/components/layout/`、`src/config/`、`src/composables/`
- [x] 1.2 修复 `src/styles/index.css`：在 `@import "tailwind.css";` 后追加 `@import "design-system.css";`
- [x] 1.3 安装 shadcn-vue 组件：sidebar、button、dropdown-menu、badge、avatar、tooltip、breadcrumb、separator

## 2. 菜单配置（纯函数，TDD 先行）

- [x] 2.1 创建 `src/config/menu.ts`：定义 `MenuItem` 类型和静态菜单配置数据
- [x] 2.2 编写 `__tests__/menu.spec.ts` 测试用例（覆盖递归生成、混合层级、空配置、meta 继承、无 path 节点跳过）
- [x] 2.3 实现 `createRoutesFromMenu()` 纯函数，递归生成 Vue Router 路由记录
- [x] 2.4 运行 `pnpm test:unit -- __tests__/menu.spec.ts` 确认全部通过

## 3. 主题持久化（Pinia Store，TDD 先行）

- [x] 3.1 编写 `__tests__/theme.spec.ts` 测试用例（覆盖默认值、localStorage 读写、toggle 切换、无效值回退）
- [x] 3.2 创建 `src/stores/theme.ts`，实现 `useThemeStore`（Setup Store 语法）
- [x] 3.3 运行 `pnpm test:unit -- __tests__/theme.spec.ts` 确认全部通过

## 4. Composables（TDD 先行）

- [x] 4.1 编写 `__tests__/menuNavigation.spec.ts` 测试用例（覆盖一级菜单切换、侧边栏数据联动、手风琴状态）
- [x] 4.2 创建 `src/composables/useMenuNavigation.ts`，实现一级菜单切换和侧边栏数据联动逻辑
- [x] 4.3 编写 `__tests__/breadcrumb.spec.ts` 测试用例（覆盖深层路径、根路径、无匹配路径）
- [x] 4.4 创建 `src/composables/useBreadcrumb.ts`，实现路径链追溯逻辑
- [x] 4.5 运行 `pnpm test:unit -- __tests__/menuNavigation.spec.ts` 和 `pnpm test:unit -- __tests__/breadcrumb.spec.ts` 确认全部通过

## 5. UI 组件（shadcn-vue，TDD 先行）

- [x] 5.1 编写 `__tests__/AppBreadcrumb.spec.ts` 测试用例（覆盖多级渲染、最后项不可点击、中间项可点击跳转）
- [x] 5.2 创建 `src/components/layout/AppBreadcrumb.vue`，使用 shadcn-vue Breadcrumb 组件
- [x] 5.3 编写 `__tests__/AppHeader.spec.ts` 测试用例（覆盖一级菜单渲染、选中态、右侧操作区事件 emit、logo 点击）
- [x] 5.4 创建 `src/components/layout/AppHeader.vue`，使用 shadcn-vue Button/DropdownMenu/Badge/Avatar 组件
- [x] 5.5 编写 `__tests__/AppSidebar.spec.ts` 测试用例（覆盖二三级渲染、手风琴、折叠切换、空菜单隐藏）
- [x] 5.6 创建 `src/components/layout/AppSidebar.vue`，使用 shadcn-vue Sidebar 区块组件和 Tooltip
- [x] 5.7 运行 `pnpm test:unit -- __tests__/AppBreadcrumb.spec.ts __tests__/AppHeader.spec.ts __tests__/AppSidebar.spec.ts` 确认全部通过

## 6. 布局集成（TDD 先行）

- [x] 6.1 编写 `__tests__/AdminLayout.spec.ts` 测试用例（覆盖完整布局渲染、子组件存在、RouterView 存在、侧边栏折叠持久化、主题 DOM 同步）
- [x] 6.2 创建 `src/layouts/AdminLayout.vue`，集成 AppHeader、AppSidebar、AppBreadcrumb 和 RouterView
- [x] 6.3 运行 `pnpm test:unit -- __tests__/AdminLayout.spec.ts` 确认全部通过

## 7. 应用入口更新

- [x] 7.1 修改 `src/router/index.ts`：调用 `createRoutesFromMenu()` 填充 routes 数组
- [x] 7.2 修改 `src/App.vue`：替换占位内容为 `<RouterView />`
- [x] 7.3 更新 `src/__tests__/App.spec.ts` 测试用例适配新 App.vue
- [x] 7.4 更新 `e2e/vue.spec.ts` E2E 测试适配新路由结构

## 8. 验证

- [x] 8.1 运行 `pnpm test:unit` 确认全部单元测试通过
- [x] 8.2 运行 `pnpm type-check` 确认无类型错误
- [x] 8.3 运行 `pnpm lint` 确认无 lint 错误
- [x] 8.4 运行 `pnpm build` 确认生产构建成功
