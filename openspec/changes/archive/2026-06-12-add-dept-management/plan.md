# 部门管理 Implementation Plan

**Goal:** 实现部门树形管理（CRUD + 树查询 + 子部门删除保护）。

**Architecture:** 后端一次查全部部门在内存构建树；前端递归组件渲染树形。

**Tech Stack:** Spring Boot 3.5 + MyBatis-Plus + MapStruct + Flyway; Vue 3.5 + shadcn-vue + Tailwind v4

---

## Task 1: 数据库迁移

```sql
CREATE TABLE sys_dept (
    dept_id    VARCHAR(32) PRIMARY KEY,
    dept_name  VARCHAR(50) NOT NULL,
    dept_code  VARCHAR(50) NOT NULL,
    parent_id  VARCHAR(32),
    sort_order INT DEFAULT 0,
    remark     VARCHAR(500),
    status     INT DEFAULT 1,
    built_in   INT DEFAULT 0,
    deleted    INT DEFAULT 0,
    create_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by  VARCHAR(32),
    update_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by  VARCHAR(32)
);
CREATE UNIQUE INDEX uk_dept_code ON sys_dept(dept_code) WHERE deleted = 0;
```

## Task 2-4: 后端

DeptDTO 特殊点：
- 含 `parentId: String` 字段
- 含 `children: List<DeptDTO>` 字段（仅树查询时填充，@TableField(exist = false) 在 entity 中不需要）
- children 在 DTO 层，不在 Entity 层

DeptService.tree() 逻辑：
1. 查询所有 deleted=0 的部门
2. 按 parentId 分组
3. 从 root 节点（parentId=null）递归构建树
4. 同级按 sortOrder 排序

DeptService.deleteById() 额外检查：
- 查是否有 parentId = deptId 且 deleted=0 的记录
- 有则抛 409

创建时 sortOrder 默认 = 同 parentId 的兄弟数量 + 1

## Task 5: 前端

DeptView.vue 与 PostView/RoleView 不同：
- 不用 Table，用递归树形渲染
- 每个树节点：展开/折叠图标 + 名称 + 编码 + 状态Switch + 操作按钮
- 顶部：新增根部门按钮
- Dialog：deptName, deptCode(编辑disabled), parentId(显示父部门名称，不可修改), sortOrder, remark
- 新增子部门时自动设置 parentId

前端树形组件方案：
- 创建 `components/system/DeptTree.vue` 递归组件
- 或在 DeptView.vue 内用 template 递归 + v-for

**验证命令：** `./mvnw test -pl backend` + `pnpm type-check`
