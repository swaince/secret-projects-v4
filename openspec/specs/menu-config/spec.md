# menu-config

## Requirements

### Requirement: 菜单项类型定义
系统 SHALL 提供 `MenuItem` TypeScript 接口定义，用于描述树形菜单数据结构。

#### Scenario: 类型结构完整性
- **WHEN** 开发者引用 `MenuItem` 类型
- **THEN** 该类型 SHALL 包含字段：`id: string`（必填）、`title: string`（必填）、`icon?: string`（可选，Lucide 图标名）、`path?: string`（可选，路由路径）、`children?: MenuItem[]`（可选，子菜单数组）、`meta?: { breadcrumb?: string; keepAlive?: boolean }`（可选）

### Requirement: 菜单树配置
系统 SHALL 支持从数组结构的静态菜单配置生成完整菜单树，根级数组项对应一级菜单。

#### Scenario: 三层菜单树
- **WHEN** 菜单配置包含一级菜单项（有 children），children 中包含二级菜单项（有 children），二级 children 中包含三级菜单项（有 path）
- **THEN** 菜单树 SHALL 正确表示三层嵌套结构，一级→二级→三级路径完整

#### Scenario: 两层菜单混合
- **WHEN** 同一菜单配置中部分一级菜单项包含三层结构、部分仅有两层（二级直接有 path 无 children）
- **THEN** 系统 SHALL 正确处理混合层级，两层项不会尝试展开不存在三层

### Requirement: 路由自动生成
`createRoutesFromMenu(menuItems)` 函数 SHALL 递归遍历菜单配置，为每个包含 `path` 的菜单节点生成 Vue Router 路由记录。

#### Scenario: 递归生成路由
- **WHEN** 传入深度为 3 层的菜单配置
- **THEN** 函数 SHALL 返回包含所有叶子节点路径的路由记录数组，每个路由记录的 meta SHALL 包含该节点及其祖先的 title 信息

#### Scenario: 路由 meta 继承
- **WHEN** 菜单节点定义了 `meta.breadcrumb` 和 `meta.keepAlive`
- **THEN** 生成的路由记录 SHALL 在 `meta` 中包含对应的 `breadcrumb` 和 `keepAlive` 值

#### Scenario: 无 path 节点跳过
- **WHEN** 菜单节点仅作为分组容器（有 children 但无 path）
- **THEN** 该节点 SHALL NOT 生成路由记录，仅其带 path 的后代节点生成路由

#### Scenario: 空菜单配置
- **WHEN** 传入空数组 `[]`
- **THEN** 函数 SHALL 返回空数组 `[]`

#### Scenario: 页面组件懒加载
- **WHEN** 为菜单节点生成路由记录
- **THEN** 路由的 `component` SHALL 使用 `() => import(...)` 动态懒加载
