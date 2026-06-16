# auth-management

授权管理 — 用户关系 + 菜单授权。

## ADDED Requirements

### Requirement: 用户关系保存
AuthController SHALL 提供 `POST /relations/{relationType}` 接口，批量保存用户关系。

#### Scenario: 绑定用户部门
- **WHEN** 请求体 userIds=["u1","u2"], targetId="deptId", relationType=DEPT
- **THEN** SHALL 在事务中先删除这些用户的 DEPT 旧关联，再批量插入新关联

### Requirement: 用户关系查询
AuthController SHALL 提供 `GET /relations/{relationType}/{userId}` 接口。

#### Scenario: 查询用户部门
- **WHEN** relationType=DEPT, userId="u1"
- **THEN** 返回该用户的所有部门ID列表

### Requirement: 菜单授权保存
AuthController SHALL 提供 `POST /authorizations/{subjectType}` 接口，批量保存菜单授权。

#### Scenario: 角色授权
- **WHEN** subjectType=ROLE, 请求体 subjectIds=["r1","r2"], menuIds=["m1","m2"]
- **THEN** SHALL 在事务中替换这些角色的菜单授权

### Requirement: 菜单授权查询
AuthController SHALL 提供 `GET /authorizations/{subjectType}/{subjectId}` 接口。

#### Scenario: 查询角色菜单
- **WHEN** subjectType=ROLE, subjectId="r1"
- **THEN** 返回该角色的 menuId 列表

### Requirement: Flyway 建表
SHALL 创建 `sys_user_relation` 和 `sys_authorization` 两张表。

#### Scenario: 表结构
- sys_user_relation: relation_id(PK), user_id, relation_type, target_id, create_at, create_by
- sys_authorization: auth_id(PK), subject_type, subject_id, menu_id, create_at, create_by

### Requirement: 枚举
SHALL 提供 `SubjectType` 和 `RelationType` 两个 @Dictionary 枚举。

### Requirement: 前端用户授权页面
UserAuthView.vue SHALL 提供4个Tab的用户授权界面。

#### Scenario: Tab切换
- **WHEN** 点击「菜单授权」Tab
- **THEN** SHALL 显示用户列表(多选) + 菜单树(复选框) + 保存

#### Scenario: 部门Tab
- **WHEN** 点击「部门」Tab
- **THEN** SHALL 显示用户列表(多选) + 部门列表(多选) + 保存

### Requirement: 前端其他授权页面
DeptAuthView/PostAuthView/RoleAuthView SHALL 提供主体的菜单授权界面。

#### Scenario: 角色授权
- **WHEN** 进入角色授权页面
- **THEN** SHALL 角色列表(多选) + 菜单树(复选框) + 保存
