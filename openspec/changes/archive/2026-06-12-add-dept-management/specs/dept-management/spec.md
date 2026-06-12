## ADDED Requirements

### Requirement: 部门树查询

系统 SHALL 提供 `GET /depts` 接口，返回完整部门树结构（无分页），每个节点 MUST 包含 children 数组。

#### Scenario: 查询完整树
- **WHEN** 用户请求 `GET /depts`
- **THEN** 返回所有未删除部门，按 parent_id 组织为树结构，根节点为 parent_id=NULL 的部门，同级按 sort_order 排序

#### Scenario: 空数据
- **WHEN** 无部门数据
- **THEN** 返回空数组

---

### Requirement: 部门详情查询

系统 SHALL 提供 `GET /depts/{deptId}` 返回单个部门信息。

#### Scenario: 存在
- **WHEN** deptId 对应未删除记录
- **THEN** 返回完整 DTO（不含 children）

#### Scenario: 不存在
- **WHEN** deptId 不存在或已删除
- **THEN** 返回 404

---

### Requirement: 创建部门

系统 SHALL 提供 `POST /depts`，dept_code MUST 唯一，parentId 可为空（创建根部门）。

#### Scenario: 创建根部门
- **WHEN** parentId 为空，deptName 和 deptCode 有效
- **THEN** 创建根部门，sortOrder 默认为同级（根级）部门数+1

#### Scenario: 创建子部门
- **WHEN** parentId 指向已存在部门
- **THEN** 创建子部门，sortOrder 默认为同级兄弟数+1

#### Scenario: 编码重复
- **WHEN** deptCode 已存在
- **THEN** 返回 409

---

### Requirement: 更新部门

系统 SHALL 提供 `PUT /depts/{deptId}`，内置部门 MUST 不可更新。

#### Scenario: 成功更新
- **WHEN** builtIn=0
- **THEN** 更新字段，返回 DTO

#### Scenario: 更新内置被拒绝
- **WHEN** builtIn=1
- **THEN** 返回 403

---

### Requirement: 删除部门

系统 SHALL 提供 `DELETE /depts/{deptId}`，有子部门时 MUST 拒绝删除，内置部门 MUST 不可删除。

#### Scenario: 成功删除叶子部门
- **WHEN** 目标部门无子部门且 builtIn=0
- **THEN** 软删除，返回 ID

#### Scenario: 有子部门时拒绝
- **WHEN** 目标部门存在未删除的子部门
- **THEN** 返回 409，提示"存在子部门，无法删除"

#### Scenario: 删除内置被拒绝
- **WHEN** builtIn=1
- **THEN** 返回 403

---

### Requirement: 批量删除部门

系统 SHALL 提供 `DELETE /depts`，跳过内置和有子部门的。

#### Scenario: 批量删除
- **WHEN** 提交 ID 列表
- **THEN** 跳过内置和有子部门的，删除其余，返回实际删除的 ID 列表

---

### Requirement: 前端部门树页面

前端 SHALL 提供 `DeptView.vue`，以树形结构展示部门。

#### Scenario: 树形展示
- **WHEN** 导航到 `/system/depts`
- **THEN** 以树形结构展示所有部门，每个节点显示名称、编码、状态、操作按钮（新增子部门/编辑/删除）

#### Scenario: 展开折叠
- **WHEN** 点击有子部门的节点
- **THEN** 展开/折叠其子节点

#### Scenario: 新增子部门
- **WHEN** 点击节点的"新增子部门"按钮
- **THEN** 打开 Dialog，parentId 自动填入当前节点 ID
