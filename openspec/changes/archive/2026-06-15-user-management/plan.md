# 用户管理模块 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现系统管理下的用户 CRUD 模块（前后端完整实现）。

**Architecture:** 后端遵循现有 CRUD 模式（Entity→DTO→Mapper→MapStruct→Service→Controller），前端遵循 PostView/DictView 模式（API 层 + 表格页面 + 字典翻译）。密码 BCrypt 加密存储。

**Tech Stack:** Spring Boot 3.5 / MyBatis-Plus / MapStruct / Flyway / BCrypt / Vue 3.5 / Pinia / shadcn-vue / Tailwind CSS v4 / Vitest

---

## Task 1: Flyway 迁移脚本

**Files:**
- Create: `backend/src/main/resources/db/migration/V1.0.0.3__create_user_table.sql`

- [ ] **Step 1: 创建建表脚本**

```sql
CREATE TABLE sys_user (
    user_id         VARCHAR(32)     PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL,
    password        VARCHAR(200)    NOT NULL,
    gender          VARCHAR(10),
    status          INT             DEFAULT 1,
    account_expire_time  TIMESTAMP,
    password_expire_time TIMESTAMP,
    last_login_time      TIMESTAMP,
    create_at       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    create_by       VARCHAR(32),
    update_at       TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(32),
    deleted         INT             DEFAULT 0
);

CREATE UNIQUE INDEX uk_sys_user_username ON sys_user(username) WHERE deleted = 0;

COMMENT ON TABLE sys_user IS '系统用户表';
```

---

## Task 2: 后端实体与 DTO

**Files:**
- Create: `backend/src/main/java/com/dfec/soft/secret/system/entity/SysUser.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/system/dto/common/UserDTO.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/system/dto/common/UserCreateDTO.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/system/dto/request/UserPageRequest.java`

- [ ] **Step 1: SysUser 实体** — 参照 SysDict 模式，@TableName("sys_user"), @TableId(type=INPUT)
- [ ] **Step 2: UserDTO** — 不含 password，用于查询响应
- [ ] **Step 3: UserCreateDTO** — 含 password(@NotBlank on Create), username(@NotBlank)
- [ ] **Step 4: UserPageRequest** — extends QueryRequest, 含 username 搜索字段

---

## Task 3: Mapper 与 MapStruct

**Files:**
- Create: `backend/src/main/java/com/dfec/soft/secret/system/mapper/UserMapper.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/system/mapstruct/UserStructMapper.java`

- [ ] **Step 1: UserMapper** — extends BaseMapper<SysUser>
- [ ] **Step 2: UserStructMapper** — entityToDTO (忽略 password), requestToEntity, updateEntity

---

## Task 4: Service 层

**Files:**
- Create: `backend/src/main/java/com/dfec/soft/secret/system/service/UserService.java`
- Create: `backend/src/main/java/com/dfec/soft/secret/system/service/impl/UserServiceImpl.java`

- [ ] **Step 1: UserService 接口** — page, create, update, deleteById, delete
- [ ] **Step 2: UserServiceImpl 实现**
  - create: 生成 UID, 检查用户名重复, BCrypt 加密密码
  - update: 若 password 为空/null 不更新密码, 否则 BCrypt 加密后更新
  - delete: 逻辑删除 (deleted=1)
  - page: 按 username 模糊搜索, 排除 deleted, 按 create_at 降序

---

## Task 5: Controller

**Files:**
- Create: `backend/src/main/java/com/dfec/soft/secret/system/controller/UserController.java`

- [ ] **Step 1: UserController**
  - GET /users — 分页查询
  - POST /users — 创建
  - PUT /users/{userId} — 修改
  - DELETE /users/{userId} — 单个删除
  - DELETE /users — 批量删除

---

## Task 6: 后端测试

**Files:**
- Create: `backend/src/test/java/com/dfec/soft/secret/system/controller/UserControllerTest.java`

- [ ] **Step 1: 编写集成测试** — 创建、查询、修改(含密码)、删除、用户名重复409
- [ ] **Step 2: 运行测试**

Run: `./mvnw test -pl backend -Dtest=UserControllerTest`

---

## Task 7: 前端 API 层

**Files:**
- Create: `frontend/src/api/user.ts`

- [ ] **Step 1: 定义接口和函数**

```typescript
export interface UserDTO { userId, username, gender, status, accountExpireTime, passwordExpireTime, lastLoginTime, createdAt }
export interface UserCreateDTO { username, password?, gender?, status? }
export interface UserPageParams { page, size, username? }
export function fetchUsers(params): Promise<PageResponse<UserDTO>>
export function createUser(data): Promise<UserDTO>
export function updateUser(userId, data): Promise<UserDTO>
export function deleteUser(userId): Promise<string>
export function deleteUsers(ids): Promise<string[]>
```

---

## Task 8: 前端页面

**Files:**
- Create: `frontend/src/views/system/UserView.vue`
- Modify: `frontend/src/config/menu.ts`

- [ ] **Step 1: UserView.vue** — 参照 PostView.vue 模式：
  - 表格：用户名、性别(字典翻译)、状态(Switch)、账号过期时间、最近登录、创建时间、操作
  - 搜索：用户名
  - 对话框：新增/编辑（用户名、密码、性别下拉、状态）
  - 删除/批量删除

- [ ] **Step 2: menu.ts 注册**

```typescript
{
  id: 'user-list',
  title: '用户管理',
  icon: 'Users',
  path: '/system/user',
  component: () => import('@/views/system/UserView.vue'),
}
```

---

## Task 9: 字典注册

**Files:**
- Modify: `frontend/src/dict/index.ts`

- [ ] **Step 1: 新增 gender 注册**

```typescript
gender: { code: 'gender', valueType: 'STRING' },
```

---

## Task 10: 验证

- [ ] **Step 1:** Run `./mvnw test -pl backend`
- [ ] **Step 2:** Run `pnpm type-check` (in frontend/)
- [ ] **Step 3:** Run `pnpm lint:oxlint` (in frontend/)
