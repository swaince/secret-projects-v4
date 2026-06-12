# 岗位管理 (Post Management) Implementation Plan

> **For agentic workers:** Use superpowers:subagent-driven-development
> to implement this plan task-by-task.

**Goal:** 实现岗位管理完整 CRUD 功能（后端 REST API + 前端管理页面），遵循字典管理模块的现有模式。

**Architecture:** Spring Boot Controller → Service → MyBatis-Plus Mapper → PostgreSQL；前端 Vue 3 + shadcn-vue 单页面表格 + Dialog 模式。

**Tech Stack:** Java 17, Spring Boot 3.5, MyBatis-Plus, MapStruct, Flyway, PostgreSQL, H2(test); Vue 3.5, TypeScript, shadcn-vue, Tailwind v4, pnpm

---

## Task 1: 数据库迁移

- [ ] **Step 1:** 创建 `backend/src/main/resources/db/migration/V1.0.0.3__create_post_table.sql`
  ```sql
  CREATE TABLE sys_post (
      post_id    VARCHAR(32) PRIMARY KEY,
      post_name  VARCHAR(50) NOT NULL,
      post_code  VARCHAR(50) NOT NULL,
      post_level INT DEFAULT 1,
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
  CREATE UNIQUE INDEX uk_post_code ON sys_post(post_code) WHERE deleted = 0;
  COMMENT ON TABLE sys_post IS '岗位表';
  -- 每列均需 COMMENT ON
  ```
- [ ] **Step 2:** 在同一迁移文件末尾插入内置数据：
  ```sql
  INSERT INTO sys_post (post_id, post_name, post_code, post_level, sort_order, built_in, status)
  VALUES ('builtin-post-operator', '操作员', 'operator', 1, 1, 1, 1);
  INSERT INTO sys_post (post_id, post_name, post_code, post_level, sort_order, built_in, status)
  VALUES ('builtin-post-auditor', '审核员', 'auditor', 2, 2, 1, 1);
  ```
- [ ] **Step 3:** 验证迁移：`./mvnw flyway:migrate -pl backend`（或启动应用自动执行）

**Commit point:** `feat(backend): add sys_post table migration`

---

## Task 2: 后端 Entity + Mapper

- [ ] **Step 1:** 创建 `backend/src/main/java/com/dfec/soft/secret/system/entity/SysPost.java`
  - @TableName("sys_post")，@TableId(type = IdType.INPUT)
  - 字段：postId, postName, postCode, postLevel, sortOrder, remark, status, builtIn, deleted, createdAt, createdBy, updatedAt, updatedBy
  - 每个字段使用 @TableField 映射下划线命名
- [ ] **Step 2:** 创建 `backend/src/main/java/com/dfec/soft/secret/system/mapper/PostMapper.java`
  - `@Mapper public interface PostMapper extends BaseMapper<SysPost> {}`

**Commit point:** `feat(backend): add SysPost entity and mapper`

---

## Task 3: 后端 DTO + MapStruct

- [ ] **Step 1:** 创建 `backend/src/main/java/com/dfec/soft/secret/system/dto/common/PostDTO.java`
  - 字段：postId, postName, postCode, postLevel, sortOrder, remark, status, builtIn, createdAt
  - @NotBlank(groups = {Create.class, Update.class}) on postName
  - @NotBlank(groups = {Create.class}) on postCode
- [ ] **Step 2:** 创建 `backend/src/main/java/com/dfec/soft/secret/system/dto/request/PostPageRequest.java`
  - extends QueryRequest
  - 字段：postName(String), postLevel(Integer)
- [ ] **Step 3:** 创建 `backend/src/main/java/com/dfec/soft/secret/system/mapstruct/PostStructMapper.java`
  - @Mapper(componentModel = "spring")
  - entityToDTO(SysPost), entityToDTO(List<SysPost>), requestToEntity(PostDTO), updateEntity(@MappingTarget SysPost, PostDTO)

**Commit point:** `feat(backend): add PostDTO, PostPageRequest and PostStructMapper`

---

## Task 4: 后端 Service 层（TDD）

- [ ] **Step 1:** 创建 Service 接口 `backend/src/main/java/com/dfec/soft/secret/system/service/PostService.java`
  - page, getById, create, update, delete, deleteById
- [ ] **Step 2:** 编写单元测试 `backend/src/test/java/com/dfec/soft/secret/system/service/PostServiceImplTest.java`
  - 测试场景：分页查询、创建成功、编码重复409、更新成功、更新内置拒绝403、删除成功、删除内置拒绝403、批量删除跳过内置
  - 使用 @SpringBootTest + H2
- [ ] **Step 3:** 实现 `backend/src/main/java/com/dfec/soft/secret/system/service/impl/PostServiceImpl.java`
  - @Service @Validated
  - 注入 PostMapper, PostStructMapper, UidService
  - CRUD 逻辑参考 DictServiceImpl
  - 内置保护：builtIn=1 时 throw OuterException(BizCode.FORBIDDEN)
  - 软删除 + 编码唯一校验
- [ ] **Step 4:** 运行测试验证：`./mvnw test -pl backend -Dtest=PostServiceImplTest`

**Commit point:** `feat(backend): add PostService with tests`

---

## Task 5: 后端 Controller 层（TDD）

- [ ] **Step 1:** 编写集成测试 `backend/src/test/java/com/dfec/soft/secret/system/controller/PostControllerTest.java`
  - @SpringBootTest + @AutoConfigureMockMvc + H2
  - 覆盖：GET /posts（分页）、GET /posts/{id}、POST /posts、PUT /posts/{id}、DELETE /posts/{id}、DELETE /posts
  - 异常场景：404、409、403、400
- [ ] **Step 2:** 实现 `backend/src/main/java/com/dfec/soft/secret/system/controller/PostController.java`
  - @RestController @RequestMapping("/posts")
  - 注入 PostService
  - 端点参考 DictController 模式
- [ ] **Step 3:** 运行全量测试：`./mvnw test -pl backend`

**Commit point:** `feat(backend): add PostController with integration tests`

---

## Task 6: 前端 API 层

- [ ] **Step 1:** 创建 `frontend/src/api/post.ts`
  - 接口：PostDTO, PostPageParams
  - 函数：fetchPosts, createPost, updatePost, deletePost, deletePosts
  - 复用 dict.ts 中的 request helper 和 R/PageResponse 类型

**Commit point:** `feat(frontend): add post API layer`

---

## Task 7: 前端页面

- [ ] **Step 1:** 创建 `frontend/src/views/system/PostView.vue` 基本结构
  - script setup: imports, state refs, API 调用函数
  - template: 搜索 Card + 操作按钮 + 表格 Card + 分页
- [ ] **Step 2:** 实现搜索功能
  - 名称 Input + 级别 Select（硬编码选项：操作员/审核员）+ 搜索/重置 Button
- [ ] **Step 3:** 实现数据表格
  - 列：复选框、名称、编码、级别、排序、状态 Switch、内置 Badge、创建时间、备注、操作（编辑/删除）
  - 行点击无特殊操作（岗位无子项）
- [ ] **Step 4:** 实现新增/编辑 Dialog
  - 表单字段：postName, postCode(编辑时 disabled), postLevel(Select), sortOrder, remark
  - 表单校验：名称/编码必填
- [ ] **Step 5:** 实现内置保护
  - builtIn=1 行：编辑/删除按钮 disabled，复选框 disabled，Switch disabled
- [ ] **Step 6:** 实现批量删除 + AlertDialog 确认
  - AlertDialogContent 添加 :disable-outside-pointer-events="true"（workaround reka-ui#2702）

**Commit point:** `feat(frontend): add PostView page`

---

## Task 8: 路由集成 + 验证

- [ ] **Step 1:** 更新 `frontend/src/config/menu.ts`，为 post-list 添加 component:
  ```typescript
  component: () => import('@/views/system/PostView.vue'),
  ```
- [ ] **Step 2:** 运行前端检查：`pnpm type-check && pnpm lint`
- [ ] **Step 3:** 运行后端全量测试：`./mvnw test -pl backend`
- [ ] **Step 4:** 启动应用端到端验证

**Commit point:** `feat(frontend): integrate post route and final verification`
