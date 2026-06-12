## Context

系统管理下需要角色管理（二级菜单）。菜单已配置（`/system/roles`），完全复用岗位管理的技术架构。

## Goals / Non-Goals

**Goals:**
- 角色 CRUD + 分页 + 搜索（名称/编码）+ 状态切换 + 批量删除 + 内置保护
- 创建时排序号默认 = 总条数 + 1

**Non-Goals:**
- 角色-权限关联、角色-用户关联、数据权限范围

## Decisions

### D1：数据模型
- **选择**：`sys_role` 表，字段 role_id/role_name/role_code/sort_order/remark + 标准字段
- **理由**：纯基础 CRUD，无额外业务字段

### D2：唯一约束
- **选择**：`role_code` partial unique index (WHERE deleted = 0)
- **理由**：与岗位一致，服务层额外校验

### D3：MapStruct updateEntity
- **选择**：ignore roleId/status/builtIn/deleted/createdAt/createdBy
- **理由**：吸取岗位管理的教训，防止主键被覆盖

## Risks / Trade-offs

无特殊风险，完全复用已验证模式。

## Migration Plan

Flyway `V1.0.0.4__create_role_table.sql`，纯新增无破坏性。

## Open Questions

无。
