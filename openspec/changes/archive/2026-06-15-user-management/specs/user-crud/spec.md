# user-crud

用户管理 CRUD 接口与前端页面规格。

## ADDED Requirements

### Requirement: 用户分页查询
UserController SHALL 提供 `GET /users` 接口，支持分页和按用户名模糊搜索。

#### Scenario: 分页查询默认列表
- **WHEN** 请求 `GET /users?page=1&size=10`
- **THEN** 返回 `PageResponse<UserDTO>`，UserDTO SHALL 不包含 password 字段

#### Scenario: 按用户名搜索
- **WHEN** 请求 `GET /users?username=admin&page=1&size=10`
- **THEN** 返回 username 包含"admin"的用户列表

### Requirement: 用户创建
UserController SHALL 提供 `POST /users` 接口，创建新用户。

#### Scenario: 创建用户
- **WHEN** 请求体包含 username="testuser", password="123456", gender="M", status=1
- **THEN** 返回创建后的 UserDTO（不含 password），密码 SHALL 以 BCrypt 加密存储

#### Scenario: 用户名重复
- **WHEN** 请求 username 已存在
- **THEN** 返回 code=409，提示用户名重复

### Requirement: 用户修改
UserController SHALL 提供 `PUT /users/{userId}` 接口，修改用户信息。

#### Scenario: 修改用户信息
- **WHEN** 请求体包含 gender="F"，userId 存在
- **THEN** 返回更新后的 UserDTO

#### Scenario: 密码为空不更新
- **WHEN** 请求体 password 为 null 或空字符串
- **THEN** SHALL 不修改数据库中的密码

#### Scenario: 密码非空则更新
- **WHEN** 请求体 password="newpass123"
- **THEN** SHALL 以 BCrypt 加密后更新密码

### Requirement: 用户删除
UserController SHALL 提供 `DELETE /users/{userId}` 接口，逻辑删除用户。

#### Scenario: 删除用户
- **WHEN** userId 存在
- **THEN** 该用户 deleted 标记为 1，返回被删 userId

### Requirement: 用户批量删除
UserController SHALL 提供 `DELETE /users` 接口，批量逻辑删除用户。

#### Scenario: 批量删除
- **WHEN** 请求体包含 ids=["id1","id2"]
- **THEN** 返回被删除的 userId 列表

### Requirement: Flyway 建表
SHALL 提供 Flyway 迁移脚本创建 `sys_user` 表。

#### Scenario: 表结构
- **WHEN** 迁移执行
- **THEN** sys_user 表 SHALL 包含：user_id(VARCHAR 32 PK), username(VARCHAR 50 UNIQUE NOT NULL), password(VARCHAR 200 NOT NULL), gender(VARCHAR 10), status(INT DEFAULT 1), account_expire_time(TIMESTAMP), password_expire_time(TIMESTAMP), last_login_time(TIMESTAMP), create_at, create_by, update_at, update_by, deleted(INT DEFAULT 0)

### Requirement: 前端用户管理页面
UserView.vue SHALL 提供用户管理的完整 CRUD 界面。

#### Scenario: 用户列表展示
- **WHEN** 进入用户管理页面
- **THEN** SHALL 以表格形式展示用户列表（用户名、性别、状态、账号过期时间、最近登录时间、创建时间）

#### Scenario: 性别列字典翻译
- **WHEN** 表格渲染性别列
- **THEN** SHALL 通过字典系统翻译显示（如 M→男, F→女）

#### Scenario: 新增用户
- **WHEN** 点击新增按钮
- **THEN** SHALL 弹出对话框，包含用户名、密码、性别、备注等字段

### Requirement: 菜单注册
`src/config/menu.ts` SHALL 在系统管理分组下注册用户管理菜单项。

#### Scenario: 菜单可见
- **WHEN** 查看系统管理菜单
- **THEN** SHALL 包含"用户管理"菜单项，路由到 `/system/user`
