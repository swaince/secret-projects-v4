## 1. 数据库迁移

- [x] 1.1 创建 Flyway 迁移脚本 `V1.0.0.3__create_post_table.sql`：建表、partial unique index、列注释
- [x] 1.2 创建 PostLevel 枚举（OPERATOR=1 操作员、AUDITOR=2 审核员），通过 @Dictionary 注册为字典

## 2. 后端数据层

- [x] 2.1 创建 Entity `SysPost.java`（@TableName("sys_post")，所有字段 + 标准审计字段）
- [x] 2.2 创建 Mapper `PostMapper.java`（extends BaseMapper<SysPost>）
- [x] 2.3 创建 DTO `PostDTO.java`（含 @NotBlank 校验 + Create/Update 分组）
- [x] 2.4 创建 `PostPageRequest.java`（extends QueryRequest，含 postName、postLevel 过滤字段）
- [x] 2.5 创建 MapStruct `PostStructMapper.java`（entityToDTO、requestToEntity、updateEntity）

## 3. 后端 Service 层

- [x] 3.1 编写 `PostService` 接口（page、getById、create、update、delete、deleteById）
- [x] 3.2 编写 `PostServiceImpl` 单元测试（覆盖 CRUD + 内置保护 + 编码冲突场景）
- [x] 3.3 实现 `PostServiceImpl`（CRUD + 软删除 + 内置保护 + 编码唯一校验）

## 4. 后端 Controller 层

- [x] 4.1 编写 `PostController` 集成测试（H2 + MockMvc，覆盖所有接口 + 异常场景）
- [x] 4.2 实现 `PostController`（@RequestMapping("/posts")，CRUD + 批量删除）

## 5. 前端 API 层

- [x] 5.1 创建 `frontend/src/api/post.ts`（PostDTO 接口、fetchPosts、createPost、updatePost、deletePost、deletePosts）

## 6. 前端页面

- [x] 6.1 创建 `frontend/src/views/system/PostView.vue`（搜索卡片 + 表格 + 分页 + Dialog 表单）
- [x] 6.2 实现搜索过滤（名称输入框 + 级别 Select + 搜索/重置按钮）
- [x] 6.3 实现表格（名称、编码、级别、排序、状态开关、内置标记、创建时间、备注、操作列）
- [x] 6.4 实现新增/编辑 Dialog（表单校验 + 级别选择）
- [x] 6.5 实现内置岗位保护（禁用编辑/删除按钮、复选框、状态开关）
- [x] 6.6 实现批量删除 + AlertDialog 确认

## 7. 路由集成

- [x] 7.1 更新 `config/menu.ts` 添加 PostView component 引用

## 8. 验证

- [x] 8.1 运行后端测试 `./mvnw test`
- [x] 8.2 运行前端 type-check + lint
- [ ] 8.3 端到端手动验证（创建/编辑/删除/内置保护/搜索/分页）
