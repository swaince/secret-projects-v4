# 授权管理 Implementation Plan

**Goal:** 两张表实现完整授权体系 — 用户关系 + 菜单授权，四种独立授权页面。

**Architecture:** sys_user_relation(relation_type区分) + sys_authorization(subject_type区分)。批量替换保存。前端四个独立页面，用户授权含4个Tab。

**Tech Stack:** Spring Boot 3.5 / MyBatis-Plus / Flyway / Vue 3.5 / shadcn-vue

---

## Task 1: Flyway 建表

**Files:**
- Create: `V1.0.1.5__create_auth_tables.sql`

```sql
CREATE TABLE sys_user_relation (
    relation_id   VARCHAR(32) PRIMARY KEY,
    user_id       VARCHAR(32) NOT NULL,
    relation_type VARCHAR(10) NOT NULL,
    target_id     VARCHAR(32) NOT NULL,
    create_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    create_by     VARCHAR(32)
);
CREATE INDEX idx_relation_user ON sys_user_relation(user_id, relation_type);

CREATE TABLE sys_authorization (
    auth_id      VARCHAR(32) PRIMARY KEY,
    subject_type VARCHAR(10) NOT NULL,
    subject_id   VARCHAR(32) NOT NULL,
    menu_id      VARCHAR(32) NOT NULL,
    create_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    create_by    VARCHAR(32)
);
CREATE INDEX idx_auth_subject ON sys_authorization(subject_type, subject_id);
```

---

## Task 2: 枚举 + Entity

- [ ] SubjectType(USER/DEPT/POST/ROLE) + RelationType(DEPT/POST/ROLE)，带 @Dictionary
- [ ] SysUserRelation.java + SysAuthorization.java

---

## Task 3: Service

- [ ] UserRelationService — saveBatch(relationType, userIds, targetId) @Transactional DELETE + INSERT; getTargetIds(relationType, userId)
- [ ] AuthorizationService — saveBatch(subjectType, subjectIds, menuIds) @Transactional DELETE + INSERT; getMenuIds(subjectType, subjectId)

---

## Task 4: Controller

- [ ] UserRelationController @RequestMapping("/relations")
  - POST /relations/{relationType} — body: {userIdIds, targetId}
  - GET /relations/{relationType}/{userId} → List<String>
- [ ] AuthController @RequestMapping("/authorizations")
  - POST /authorizations/{subjectType} — body: {subjectIds, menuIds}
  - GET /authorizations/{subjectType}/{subjectId} → List<String>

---

## Task 5: 前端 API

- [ ] `src/api/auth.ts` — fetchAuthMenuIds, saveAuth, fetchUserRelations, saveUserRelations

---

## Task 6: 前端页面

**UserAuthView.vue**（4个Tab）:
- Tab1(菜单): 用户多选列表 + 菜单树复选框 + 保存
- Tab2(部门): 用户多选列表 + 部门多选列表 + 保存
- Tab3(岗位): 同上
- Tab4(角色): 同上

**DeptAuthView/PostAuthView/RoleAuthView**:
- 主体多选列表 + 菜单树复选框 + 保存
- RoleAuthView 在菜单树中排除按钮类型（角色通常只授权到菜单级）

---

## Task 7: 菜单数据

- Flyway: 授权管理(D, parent=系统管理) → 用户授权(M)/部门授权(M)/岗位授权(M)/角色授权(M)

---

## Task 8: 验证

- [ ] `./mvnw test -pl backend`
- [ ] `pnpm type-check` + `pnpm lint`
