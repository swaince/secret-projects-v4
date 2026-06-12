# 角色管理 Implementation Plan

**Goal:** 实现角色基础 CRUD，完全复用岗位管理模式。

**Tech Stack:** Spring Boot 3.5 + MyBatis-Plus + MapStruct + Flyway + PostgreSQL; Vue 3.5 + shadcn-vue + Tailwind v4

---

## Task 1-2: 数据库 + 数据层

参考 `V1.0.0.3__create_post_table.sql` 和 `SysPost.java` 模式。

表：sys_role (role_id, role_name, role_code, sort_order, remark, status, built_in, deleted, create_at, create_by, update_at, update_by)

## Task 3-4: Service + Controller (TDD)

参考 PostServiceImpl + PostController 模式。
- 创建时 sortOrder 默认 = 总条数 + 1
- MapStruct updateEntity 使用 @Mapping(ignore) 忽略 roleId/status/builtIn/deleted/createdAt/createdBy

## Task 5: 前端

参考 PostView.vue 模式。
- 搜索：roleName + roleCode（无级别字段）
- 表格列：名称、编码、排序、状态、内置、创建时间、备注、操作
- 无"详情"按钮（角色暂无子项）

**验证命令：** `./mvnw test -pl backend` + `pnpm type-check`
