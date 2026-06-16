## Context

系统已有用户、角色、部门、岗位、菜单管理。需要建立多维度授权绑定。

## Goals / Non-Goals

**Goals:**
- 菜单授权：四种主体（用户/部门/岗位/角色）均可绑定菜单权限（sys_authorization 表）
- 用户关系：用户可绑定多个部门/岗位/角色（独立关联表）
- 四种授权页面各自独立
- 支持批量授权（多选主体统一赋权）
- 用户授权页面支持4个Tab（菜单/部门/岗位/角色）

**Non-Goals:**
- 不做运行时权限校验
- 不做授权审批流程

## Decisions

### D1：用户关系表

- **选择**：`sys_user_relation(relation_id, user_id, relation_type, target_id, create_at, create_by)`
- `relation_type` = DEPT/POST/ROLE，通过类型字段区分，一张表覆盖三种用户关联
- 批量保存：事务中 DELETE 旧关联 → INSERT 新关联

### D2：菜单授权表

- **选择**：`sys_authorization(auth_id, subject_type, subject_id, menu_id, create_at, create_by)`
- `subject_type` = USER/DEPT/POST/ROLE，四种主体类型共用一张菜单授权表
- 批量保存：事务中 DELETE 旧授权 → INSERT 新授权

### D3：枚举

- **选择**：两个 `@Dictionary` 枚举
  - `SubjectType`：USER=用户, DEPT=部门, POST=岗位, ROLE=角色
  - `RelationType`：DEPT=部门, POST=岗位, ROLE=角色

### D3：页面拆分

- **选择**：四个独立页面（user/DeptAuth/PostAuth/RoleAuth）
- 用户授权：4个Tab（菜单授权/部门绑定/岗位绑定/角色绑定）
- 部门/岗位/角色授权：菜单授权 + 批量选择

### D4：批量授权

- **选择**：支持多选主体。选择N个用户 → 赋予相同的菜单/部门/岗位/角色
- 后端接收主体ID列表，遍历保存

## Risks / Trade-offs

[Trade-off] 四张表 → 接受：FK约束清晰，查询简单

## Open Questions

无
