## ADDED Requirements

### Requirement: 角色分页查询

系统 SHALL 提供角色分页查询接口 `GET /roles`，支持按角色名称和编码过滤。

#### Scenario: 无条件分页查询
- **WHEN** 用户请求 `GET /roles?page=1&size=10`
- **THEN** 返回第一页最多 10 条未删除的角色记录

#### Scenario: 按名称模糊搜索
- **WHEN** 用户请求 `GET /roles?page=1&size=10&roleName=管理`
- **THEN** 仅返回 role_name 包含「管理」的记录

#### Scenario: 按编码模糊搜索
- **WHEN** 用户请求 `GET /roles?page=1&size=10&roleCode=admin`
- **THEN** 仅返回 role_code 包含「admin」的记录

---

### Requirement: 角色详情查询

系统 SHALL 提供 `GET /roles/{roleId}` 返回单个角色完整信息。

#### Scenario: 查询存在的角色
- **WHEN** roleId 对应未删除记录
- **THEN** 返回完整 DTO

#### Scenario: 查询不存在的角色
- **WHEN** roleId 不存在或已删除
- **THEN** 返回 404

---

### Requirement: 创建角色

系统 SHALL 提供 `POST /roles`，role_code MUST 唯一。

#### Scenario: 成功创建
- **WHEN** roleName 非空、roleCode 非空且唯一
- **THEN** 创建记录，sortOrder 默认为总条数+1，返回 DTO

#### Scenario: 编码重复
- **WHEN** roleCode 已存在
- **THEN** 返回 409

#### Scenario: 必填缺失
- **WHEN** roleName 或 roleCode 为空
- **THEN** 返回 400

---

### Requirement: 更新角色

系统 SHALL 提供 `PUT /roles/{roleId}`，内置角色 MUST 不可更新。

#### Scenario: 成功更新
- **WHEN** 目标角色 builtIn=0
- **THEN** 更新字段，返回 DTO

#### Scenario: 更新内置角色被拒绝
- **WHEN** 目标角色 builtIn=1
- **THEN** 返回 403

---

### Requirement: 删除角色

系统 SHALL 提供 `DELETE /roles/{roleId}`，软删除，内置角色 MUST 不可删除。

#### Scenario: 成功删除
- **WHEN** builtIn=0
- **THEN** 软删除，返回 ID

#### Scenario: 删除内置被拒绝
- **WHEN** builtIn=1
- **THEN** 返回 403

---

### Requirement: 批量删除角色

系统 SHALL 提供 `DELETE /roles`，跳过内置角色。

#### Scenario: 批量删除
- **WHEN** 提交 ID 列表
- **THEN** 跳过内置，删除其余，返回实际删除的 ID 列表

---

### Requirement: 前端角色管理页面

前端 SHALL 提供 `RoleView.vue`，挂载到 `/system/roles`。

#### Scenario: 页面展示
- **WHEN** 导航到 `/system/roles`
- **THEN** 搜索卡片（名称 + 编码 + 搜索/重置）+ 表格 + 分页 + Dialog

#### Scenario: 内置保护
- **WHEN** builtIn=1
- **THEN** 编辑/删除/复选框/状态开关 disabled
