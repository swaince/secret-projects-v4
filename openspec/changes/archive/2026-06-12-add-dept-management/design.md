## Context

系统管理需要部门管理（二级菜单）。与岗位/角色不同，部门为树形结构（parent_id 自关联），API 返回完整树，前端渲染树形视图。

## Goals / Non-Goals

**Goals:**
- 部门 CRUD + 树形查询（无分页）
- 支持多根部门、多层级
- 状态切换 + 内置保护
- 有子部门时禁止删除
- 创建时 sortOrder 默认 = 同级部门数 + 1

**Non-Goals:**
- 部门-用户关联
- 部门数据权限

## Decisions

### D1：树形存储
- **选择**：adjacency list（parent_id 字段）
- **理由**：最简单，查询量小（部门通常不超过几百条）
- **已考虑**：nested set / closure table — 过度设计

### D2：树构建方式
- **选择**：后端一次查全部未删除记录，内存中构建树返回
- **理由**：部门数据量小，一次查询效率最高

### D3：删除保护
- **选择**：有子部门时返回 409 冲突
- **理由**：防止意外删除整个子树，保持数据完整性

### D4：前端树组件
- **选择**：递归组件渲染树形结构
- **理由**：shadcn-vue 无内置 Tree 组件，自定义递归最灵活

## Risks / Trade-offs

- [Trade-off] 一次加载全部部门 → 接受：部门数据量小（通常 < 500），不影响性能

## Migration Plan

Flyway `V1.0.0.5__create_dept_table.sql`，纯新增。

## Open Questions

无。
