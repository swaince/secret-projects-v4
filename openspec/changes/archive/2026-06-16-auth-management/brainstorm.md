# 授权管理 — 头脑风暴记录

## 背景

系统已有用户、部门、岗位、角色、菜单。需要多维度授权绑定。

## 决议链

### Q1：授权模型？

**决定**：两张表，按业务域拆分。
1. **用户关系表** `sys_user_relation(relation_id, user_id, relation_type, target_id, create_at, create_by)`
   - `relation_type` = DEPT/POST/ROLE
   - 用户→部门: `user_id, DEPT, dept_id`
   - 用户→岗位: `user_id, POST, post_id`
   - 用户→角色: `user_id, ROLE, role_id`

2. **菜单授权表** `sys_authorization(auth_id, subject_type, subject_id, menu_id, create_at, create_by)`
   - `subject_type` = USER/DEPT/POST/ROLE
   - 用户→菜单: `USER, userId, menuId`
   - 角色→菜单: `ROLE, roleId, menuId`

### Q2：表数量？

**决定**：两张表（用户关系 + 菜单授权），各自类型字段区分，独立查询互不干扰。

### Q2：页面拆分？

**决定**：四种授权各自独立页面
- **用户授权**：包含4个Tab — 菜单授权、部门绑定、岗位绑定、角色绑定
- **部门授权**：选择部门 → 批量绑定菜单
- **岗位授权**：选择岗位 → 批量绑定菜单
- **角色授权**：选择角色 → 批量绑定菜单

### Q3：批量授权

**决定**：支持多选主体。选择多个用户/部门/岗位/角色，统一赋予相同菜单/关联。

### Q4：交互设计

**决定**：左侧多选主体列表 + 右侧树形复选框（菜单授权）/ 列表多选（关系绑定）+ 保存按钮
