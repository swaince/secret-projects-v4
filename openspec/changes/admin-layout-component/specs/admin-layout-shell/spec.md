## ADDED Requirements

### Requirement: 布局结构
AdminLayout 组件 SHALL 提供三段式布局结构：顶部通栏（AppHeader）、左侧侧边栏（AppSidebar）、主内容区（含面包屑 AppBreadcrumb 和 RouterView）。

#### Scenario: 完整布局渲染
- **WHEN** AdminLayout 组件挂载
- **THEN** 页面 SHALL 从上到下渲染 AppHeader 固定通栏、下方左右分栏（左侧 AppSidebar、右侧主内容区），主内容区顶部包含 AppBreadcrumb，下方为 RouterView

#### Scenario: 内容区全宽
- **WHEN** 当前已激活的一级菜单无 children（即侧边栏无菜单项可展示）
- **THEN** 侧边栏 SHALL 隐藏，主内容区 SHALL 占满全部可用宽度

### Requirement: 顶部通栏
AppHeader 组件 SHALL 渲染三区域：左侧系统 logo + 名称、中间一级菜单导航项、右侧操作区（消息通知、主题切换、用户头像）。

#### Scenario: Logo 和名称
- **WHEN** 传入 `logo` 和 `title` props
- **THEN** 左侧区 SHALL 显示 logo 图片和系统名称文字，点击 SHALL 导航至首页 `/`

#### Scenario: 一级菜单渲染
- **WHEN** 菜单配置中有多个一级菜单项
- **THEN** 中间区 SHALL 以横向排列渲染所有一级项，每项显示图标 + 文字，当前选中项 SHALL 高亮

#### Scenario: 一级菜单点击
- **WHEN** 用户点击某个一级菜单项
- **THEN** 浏览器 SHALL 导航至该菜单项配置的 path 路由，该一级菜单项 SHALL 变为选中态

#### Scenario: 消息通知
- **WHEN** 有未读通知数量传入
- **THEN** 通知图标 SHALL 显示未读数 badge，点击 SHALL 弹出 DropdownMenu 通知面板

#### Scenario: 主题切换按钮
- **WHEN** 用户点击主题切换图标按钮
- **THEN** 系统 SHALL 调用 `useThemeStore().toggle()` 切换亮色/暗色主题，图标 SHALL 随当前主题切换（亮色显示月亮，暗色显示太阳）

#### Scenario: 用户头像下拉
- **WHEN** 用户点击头像区域
- **THEN** SHALL 弹出 DropdownMenu 包含用户信息和退出登录选项

### Requirement: 侧边栏菜单
AppSidebar 组件 SHALL 根据当前激活的一级菜单渲染对应的二/三级菜单，采用手风琴模式展开。

#### Scenario: 侧边栏数据联动
- **WHEN** 一级菜单切换至某个包含 children 的项
- **THEN** 侧边栏 SHALL 展示该一级菜单下的所有二级菜单项及其三级子项

#### Scenario: 手风琴展开
- **WHEN** 用户点击某个已收起的二级菜单项（该二级菜单有 children）
- **THEN** 该二级菜单 SHALL 展开显示三级子项，同时其他已展开的二级菜单 SHALL 自动收起

#### Scenario: 手风琴再次点击收起
- **WHEN** 用户点击当前已展开的二级菜单项
- **THEN** 该二级菜单 SHALL 收起，隐藏其三级子项

#### Scenario: 二级菜单直接跳转
- **WHEN** 用户点击某个有 path 但无 children 的二级菜单项
- **THEN** 浏览器 SHALL 直接导航至该 path，该菜单项 SHALL 高亮选中

#### Scenario: 三级菜单点击跳转
- **WHEN** 用户点击某个有 path 的三级菜单项
- **THEN** 浏览器 SHALL 导航至该 path，该菜单项 SHALL 高亮选中

### Requirement: 侧边栏折叠
AppSidebar 组件 SHALL 支持折叠/展开切换，由底部折叠按钮触发。

#### Scenario: 折叠切换
- **WHEN** 用户点击侧边栏底部的折叠按钮
- **THEN** 侧边栏 SHALL 在展开态（240px 宽，显示图标+文字）和折叠态（64px 宽，仅显示图标）之间切换

#### Scenario: 折叠态交互
- **WHEN** 侧边栏处于折叠态，用户 hover 菜单图标
- **THEN** SHALL 显示 Tooltip 展示完整菜单标题

#### Scenario: 折叠态二级菜单展开
- **WHEN** 侧边栏处于折叠态，用户点击某个二级菜单图标
- **THEN** 该二级菜单的三级子项 SHALL 以 floating popover 形式弹出展示

#### Scenario: 折叠态持久化
- **WHEN** 用户折叠/展开侧边栏后刷新页面
- **THEN** 侧边栏折叠态 SHALL 保持用户上次的选择（通过 localStorage 持久化）

### Requirement: 面包屑导航
AppBreadcrumb 组件 SHALL 基于当前路由路径自动生成面包屑链。

#### Scenario: 深层路径面包屑
- **WHEN** 当前路由路径为多层嵌套（如 `/system/users`）
- **THEN** 面包屑 SHALL 显示完整路径链 "系统管理 > 用户管理 > 用户列表"，中间项可点击跳转，最后一项不可点击

#### Scenario: 根路径面包屑
- **WHEN** 当前路由路径为一级菜单首页（如 `/dashboard`）
- **THEN** 面包屑 SHALL 仅显示该菜单项标题（如 "仪表盘"），该项不可点击

#### Scenario: 未匹配路径
- **WHEN** 当前路由路径在菜单配置中无法匹配
- **THEN** 面包屑 SHALL 回退显示 route.matched 中的 meta.title（如可用），或无面包屑
