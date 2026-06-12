## Why

组织架构管理需要部门功能，部门是用户归属和数据权限的基础。当前无任何部门代码。部门为树形结构（上下级关系），与扁平 CRUD 不同。

## What Changes

**后端**
- sys_dept 表（含 parent_id 自关联）
- Entity / DTO / Mapper / MapStruct / Service / Controller
- `GET /depts` 返回树结构，`DELETE` 有子部门保护

**前端**
- api/dept.ts + views/system/DeptView.vue（树形视图）
- menu.ts 调整为二级菜单

## Capabilities

### New Capabilities
- `dept-management`: 部门树形管理（CRUD + 树查询 + 子部门删除保护）

### Modified Capabilities
（无）

## Impact

- 数据库：新增 sys_dept 表
- 后端/前端：新增一组文件
- menu.ts：dept-list 从 org-mgmt 移至 system 二级
- 无破坏性变更
