# brainstord: admin-layout-component

## 背景

secret-projects-v4 前端处于脚手架初始状态：
- `src/router/index.ts` routes 为空数组
- `App.vue` 无 `<RouterView />`
- 无 `src/layouts/`、`src/components/`、`src/views/`、`src/composables/`
- shadcn-vue 已配置但未安装任何组件
- 两套设计系统文件：`tailwind.css`（中性灰，实际生效）和 `design-system.css`（紫色调，未引入）
- `design-system.css` 在 AGENTS.md 中声明为"具有最终决定权"，但未被 `index.css` 引入

## 决策链

### Q1: 布局结构

三种典型后台布局方案：

| 方案 | 描述 | 取舍 |
|------|------|------|
| A: 顶部通栏 + 左侧侧边栏 | 上方横条放 logo + 一级菜单，左侧侧边栏动态展示子菜单 | 空间利用率高，三层菜单清晰 |
| B: 纯顶部导航 | 所有层级在顶栏，深层用下拉 | 内容全宽但深层操作路径长 |
| C: 顶部两行 + 无侧边栏 | 顶部一二两级，三级用 Tab | 顶栏占空间过大 |

**选 A**：顶部通栏 + 左侧侧边栏，最经典的后台布局。

### Q2: 菜单数据来源

| 方案 | 描述 |
|------|------|
| A: 静态配置 | 写死在路由/菜单配置文件 |
| B: 后端 API 动态返回 | 权限控制，不同用户不同菜单 |
| C: 混合模式 | 前端定义完整菜单树，后端返回权限交集 |

**选 A（当前）→ 后期迁移至 B**：先静态配置，后期替换数据源即可。设计上数据层抽象，`createRoutesFromMenu` 函数接受通用 MenuItem 数组输入。

### Q3: 侧边栏手风琴 vs 独立折叠

| 方案 | 描述 | 取舍 |
|------|------|------|
| A: 手风琴式 | 同一时间只有一个二级菜单展开 | 界面整洁，操作专注 |
| B: 独立折叠 | 多个二级菜单可同时展开 | 灵活但视觉杂乱 |
| C: 混合式 | 图标模式手风琴，全宽模式独立 | 复杂 |

**选 A**：手风琴式展开。

### Q4: 响应式策略

| 方案 | 描述 |
|------|------|
| A: drawer 模式 | 窄屏隐藏，汉堡按钮 overlay |
| B: 图标模式 | 移动端默认收起为图标 |
| C: 不做响应式 | 仅桌面端 |

**选 C**：仅桌面端使用，不做移动端适配。

### Q5: 主题切换方式

| 方案 | 描述 |
|------|------|
| A: 三态循环 | 亮色 / 暗色 / 跟随系统 |
| B: 二态 toggle | 开关形式 |
| C: 二态图标按钮 | 太阳/月亮图标切换 |

**选 C**：亮色 / 暗色二态，图标按钮切换。Pinia store 管理状态，localStorage 持久化。

### Q6: 设计系统选择

`tailwind.css`（中性灰，实际生效）vs `design-system.css`（紫色调，AGENTS.md 声明为权威但未引入）。

**选 A（修复）**：在 `index.css` 中 `tailwind.css` 之后追加 `@import "design-system.css";`，用 design-system.css 覆盖默认变量。

### Q7: 侧边栏折叠功能

| 方案 | 描述 |
|------|------|
| A: 可折叠 | 折叠按钮，图标模式/全宽切换 |
| B: 不折叠 | 始终全宽 |
| C: 可拖拽 | 用户拖拽调整宽度 |

**选 A**：始终有折叠按钮。全宽 240px，折叠 64px（仅图标）。折叠态下二级菜单 floating popover 展开。

### Q8: 一级菜单点击行为

| 方案 | 描述 |
|------|------|
| A: 切换式 | 点击切换侧边栏内容，不触发路由跳转 |
| B: 导航式 | 点击跳转模块首页，侧边栏根据路由自动展示 |

**选 B**：一级菜单直接跳转，侧边栏根据当前路由自动显示对应子菜单。

### Q9: 菜单配置→路由策略

| 方案 | 描述 |
|------|------|
| A: 全量注册 | 所有带 path 节点注册为路由 |
| B: 按需注册 | 动态 addRoute |
| C: 延迟注册 | 首次进入时注册 |

**选 A**：全量注册，最简单，后期对接权限只需加守卫。

## 设计总览

### 组件架构

```
src/
├── layouts/
│   └── AdminLayout.vue              # 布局壳（顶部 + 侧边栏 + 主内容）
├── components/
│   └── layout/
│       ├── AppHeader.vue            # 顶部通栏
│       ├── AppSidebar.vue           # 左侧边栏
│       └── AppBreadcrumb.vue        # 面包屑导航
├── config/
│   └── menu.ts                      # 菜单配置 + MenuItem 类型定义
├── composables/
│   ├── useMenuNavigation.ts         # 一级菜单切换 + 侧边栏状态
│   └── useBreadcrumb.ts             # 面包屑链路计算
├── stores/
│   └── theme.ts                     # 主题状态持久化
├── styles/
│   └── index.css                    # + @import "design-system.css";
```

### 布局骨架

```
┌──────────────────────────────────────────────────────────┐
│  AppHeader（固定顶部 h-14）                                │
│  [Logo]  [一级1] [一级2] [一级3] ...          [🔔] [🌙] [头像▾] │
├────────┬─────────────────────────────────────────────────┤
│  App   │  AppBreadcrumb 面包屑                            │
│ Sidebar│─────────────────────────────────────────────────│
│        │  <RouterView /> 主内容区                         │
│        │                                                 │
└────────┴─────────────────────────────────────────────────┘
```

### 菜单数据结构

```typescript
interface MenuItem {
  id: string
  title: string
  icon?: string           // Lucide 图标名
  path?: string           // 路由路径（叶子节点）
  children?: MenuItem[]   // 子菜单
  meta?: {
    breadcrumb?: string
    keepAlive?: boolean
  }
}
```

- 一级菜单 = `MenuConfig` 数组根级
- `path` + 无 `children` = 叶子路由节点
- `children` 存在 = 非叶子，展开子菜单

### 顶部通栏（AppHeader）

- **左侧区**：系统 Logo + 名称（props），点击跳转首页
- **中间区**：一级菜单从 menuConfig 根级渲染，当前选中高亮
- **右侧操作区**：
  - 🔔 消息通知图标 + 未读 badge → DropdownMenu（预留数据接口）
  - 🌙/☀️ 主题切换图标按钮 → `useThemeStore().toggle()`
  - 用户头像 + 名称 → DropdownMenu（用户信息、退出登录）

### 侧边栏（AppSidebar）

- 手风琴展开：同一时间仅一个二级菜单展开
- 二级有 `path` 无 `children` → 直接路由跳转
- 二级有 `children` → 点击展开/收起
- 三级有 `path` → 点击跳转，高亮选中
- 切换一级菜单 → 侧边栏自动切换对应数据
- 空菜单（一级无 children）→ 侧边栏隐藏，内容区全宽
- 折叠逻辑：
  - 全宽 240px（图标+文字）
  - 折叠 64px（仅图标 + tooltip）
  - 折叠态二级菜单 floating popover 展开

### 面包屑（AppBreadcrumb）

- 基于 `route.matched` + `menuConfig` 树追溯路径链
- 最后一项不可点击（当前页），其余可点击跳转
- `useBreadcrumb` composable 实现

### 主题切换

- Pinia store：`useThemeStore`，状态 `'light' | 'dark'`
- localStorage key：`app-theme`
- 默认 'light'
- 在 AdminLayout 中 watch theme，操作 `document.documentElement.classList` 添加/移除 `dark` class
- 图标按钮切换，当前亮色显示月亮（切暗），暗色显示太阳（切亮）

### 路由生成

- `createRoutesFromMenu(menuConfig)` 纯函数
- 递归遍历菜单树，将带 `path` 的节点转为路由记录
- 路由 meta 包含 `title`、`breadcrumb`、`keepAlive` 等
- 所有页面组件使用 `() => import(...)` 懒加载

## 实现依赖

### shadcn-vue 组件（需安装）

| 组件 | 用途 | 
|------|------|
| `sidebar` | 侧边栏主体（SidebarProvider, Sidebar, SidebarContent, SidebarMenu 等）|
| `button` | 按钮（折叠、主题切换）|
| `dropdown-menu` | 通知面板、用户下拉菜单 |
| `badge` | 消息未读数 |
| `avatar` | 用户头像 |
| `tooltip` | 折叠态图标 hover 提示 |
| `breadcrumb` | 面包屑组件 |
| `separator` | 分隔线 |

### CSS 修复

`src/styles/index.css` 追加 `@import "design-system.css";`，在 `tailwind.css` 之后加载。

### 目录初始化

需创建：`src/layouts/`、`src/components/layout/`、`src/config/`、`src/composables/`

## 测试策略（强制 TDD）

AGENTS.md 要求强制 TDD。编写顺序：纯函数/逻辑 → composables → stores → 组件。

| 测试目标 | 文件 | 测试内容 |
|---------|------|---------|
| `createRoutesFromMenu` | `__tests__/menu.spec.ts` | 菜单→路由转换，递归、空节点、path 缺失 |
| `useBreadcrumb` | `__tests__/breadcrumb.spec.ts` | 路径→面包屑链，深层嵌套、无匹配 |
| `useThemeStore` | `__tests__/theme.spec.ts` | toggle、localStorage 读写、默认值 |
| `useMenuNavigation` | `__tests__/menuNavigation.spec.ts` | 一级菜单切换、侧边栏联动、手风琴 |
| `AppHeader` | `__tests__/AppHeader.spec.ts` | 菜单渲染、选中态、事件 emit |
| `AppSidebar` | `__tests__/AppSidebar.spec.ts` | 菜单渲染、手风琴、折叠、空状态 |
| `AppBreadcrumb` | `__tests__/AppBreadcrumb.spec.ts` | 路径链渲染、交互 |
| `AdminLayout` | `__tests__/AdminLayout.spec.ts` | 整体集成 |
