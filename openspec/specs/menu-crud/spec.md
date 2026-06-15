# menu-crud Specification

## Purpose
TBD - created by archiving change menu-management. Update Purpose after archive.
## Requirements
### Requirement: 菜单树查询
MenuController SHALL 提供 `GET /menus/tree` 接口，返回完整菜单树（已组装为层级结构）。

#### Scenario: 获取完整菜单树
- **WHEN** 请求 `GET /menus/tree`
- **THEN** 返回所有 status=1 且 deleted=0 的菜单，按 parentId 组装为树结构，每层按 sortOrder 升序

#### Scenario: 空菜单
- **WHEN** 系统无任何菜单数据
- **THEN** 返回空数组

### Requirement: 菜单列表查询
MenuController SHALL 提供 `GET /menus` 接口，返回平铺的全部菜单列表（不分页）。

#### Scenario: 获取全部菜单
- **WHEN** 请求 `GET /menus`
- **THEN** 返回所有 deleted=0 的菜单平铺列表，按 sortOrder 升序

### Requirement: 菜单创建
MenuController SHALL 提供 `POST /menus` 接口，创建菜单项。

#### Scenario: 创建目录
- **WHEN** 请求体 menuType="D", menuName="系统管理", icon="Settings", sortOrder=1
- **THEN** 返回创建后的 MenuDTO，parentId 为 null 表示根级

#### Scenario: 创建菜单
- **WHEN** 请求体 menuType="M", menuName="用户管理", path="/system/users", component="system/UserView", parentId="xxx"
- **THEN** 返回创建后的 MenuDTO

#### Scenario: 创建按钮
- **WHEN** 请求体 menuType="B", menuName="新增用户", permission="system:user:add", parentId="xxx"
- **THEN** 返回创建后的 MenuDTO

### Requirement: 菜单修改
MenuController SHALL 提供 `PUT /menus/{menuId}` 接口，修改菜单项。

#### Scenario: 修改菜单名称
- **WHEN** 请求体 menuName="用户列表"
- **THEN** 返回更新后的 MenuDTO

#### Scenario: 不允许将自己设为父级
- **WHEN** 请求体 parentId 等于当前 menuId
- **THEN** 返回错误提示

### Requirement: 菜单删除
MenuController SHALL 提供 `DELETE /menus/{menuId}` 接口，逻辑删除菜单项。

#### Scenario: 删除叶子节点
- **WHEN** menuId 无子节点
- **THEN** 逻辑删除该菜单，返回被删 menuId

#### Scenario: 删除含子节点的菜单
- **WHEN** menuId 有子节点
- **THEN** SHALL 级联逻辑删除所有子孙节点

### Requirement: Flyway 建表
SHALL 提供 Flyway 迁移脚本创建 `sys_menu` 表。

#### Scenario: 表结构
- **WHEN** 迁移执行
- **THEN** sys_menu 表 SHALL 包含：menu_id(VARCHAR 32 PK), parent_id(VARCHAR 32), menu_name(VARCHAR 50 NOT NULL), menu_type(VARCHAR 1 NOT NULL), path(VARCHAR 200), component(VARCHAR 200), icon(VARCHAR 50), sort_order(INT DEFAULT 0), status(INT DEFAULT 1), permission(VARCHAR 100), visible(INT DEFAULT 1), redirect(VARCHAR 200), require_auth(INT DEFAULT 1), create_at, create_by, update_at, update_by, deleted(INT DEFAULT 0)

### Requirement: MenuType 枚举
SHALL 提供 `MenuType` 枚举并标注 `@Dictionary`，自动同步到字典表。

#### Scenario: 枚举值
- **WHEN** 应用启动
- **THEN** 字典表 SHALL 包含 menu_type 字典：D=目录, M=菜单, B=按钮

### Requirement: 前端菜单管理页面
MenuView.vue SHALL 提供菜单的树形 CRUD 界面。

#### Scenario: 树形展示
- **WHEN** 进入菜单管理页面
- **THEN** SHALL 以树形表格展示菜单（名称、图标、类型、路径、权限标识、排序、状态）

#### Scenario: 新增菜单
- **WHEN** 点击新增按钮
- **THEN** SHALL 弹出对话框，根据 menuType 动态显示相关字段

### Requirement: 前端动态菜单加载
应用 SHALL 从接口动态加载菜单树并注册路由，替代静态 menu.ts。

#### Scenario: 启动加载菜单
- **WHEN** 应用启动（或用户登录后）
- **THEN** SHALL 请求 `GET /menus/tree` 获取菜单数据，生成路由并调用 `router.addRoute()` 注册

#### Scenario: 组件动态导入
- **WHEN** 菜单项 component="system/UserView"
- **THEN** SHALL 使用 `() => import('@/views/system/UserView.vue')` 动态加载组件

#### Scenario: 按钮类型不生成路由
- **WHEN** 菜单项 menuType="B"
- **THEN** SHALL 不注册路由，仅保留 permission 信息

