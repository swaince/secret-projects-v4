# 部门管理 (Dept Management) — Brainstorm

## 背景

系统管理模块需要部门管理功能（二级菜单）。部门存在上下级层级关系，允许多个根部门。前端展示部门树，不做分页。

## 决议链

### Q1: 部门字段？
**决议**：基础字段
- dept_name — 名称（必填）
- dept_code — 编码（必填，唯一）
- parent_id — 父部门ID（NULL 表示根部门）
- sort_order — 排序号
- remark — 备注

### Q2: API 设计？
**决议**：
- `GET /depts` — 返回完整树结构（无分页），每个节点含 children 数组
- `GET /depts/{deptId}` — 单个部门详情
- `POST /depts` — 创建（含 parentId）
- `PUT /depts/{deptId}` — 更新
- `DELETE /depts/{deptId}` — 删除（有子部门时拒绝）
- `DELETE /depts` — 批量删除

### Q3: 前端展示？
**决议**：树形列表，每个节点展示名称/编码/状态/操作，可展开/折叠子节点。新增/编辑用 Dialog。

### Q4: 删除策略？
**决议**：有子部门时禁止删除（返回 409），必须先删子部门。内置部门不可删除。
