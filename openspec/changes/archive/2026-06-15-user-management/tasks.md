## 1. 后端：数据库迁移

- [x] 1.1 创建 Flyway 迁移脚本 `V1.0.0.3__create_user_table.sql`（sys_user 表）

## 2. 后端：实体与 DTO

- [x] 2.1 创建 `SysUser` 实体类（MyBatis-Plus @TableName + @TableId）
- [x] 2.2 创建 `UserDTO`（不含 password 的响应 DTO）
- [x] 2.3 创建 `UserCreateDTO`（含 password 的创建请求 DTO）
- [x] 2.4 创建 `UserPageRequest` 分页请求（继承 QueryRequest，含 username 搜索）

## 3. 后端：Mapper 与 MapStruct

- [x] 3.1 创建 `UserMapper` 接口（extends BaseMapper<SysUser>）
- [x] 3.2 创建 `UserStructMapper`（entity ↔ DTO 转换，忽略 password 映射到响应 DTO）

## 4. 后端：Service

- [x] 4.1 创建 `UserService` 接口（page, create, update, delete, deleteBatch）
- [x] 4.2 创建 `UserServiceImpl` 实现（BCrypt 加密密码、用户名重复检查、修改时密码为空不更新）

## 5. 后端：Controller

- [x] 5.1 创建 `UserController`（@RequestMapping("/users")，CRUD 端点）

## 6. 后端：测试

- [x] 6.1 编写 `UserControllerTest` 集成测试（创建、查询、修改、删除、用户名重复）

## 7. 前端：API 层

- [x] 7.1 创建 `src/api/user.ts`（UserDTO, UserCreateDTO, fetchUsers, createUser, updateUser, deleteUser, deleteUsers）

## 8. 前端：页面

- [x] 8.1 创建 `src/views/system/UserView.vue`（表格 + 搜索 + 对话框 CRUD + 状态 Switch + 性别字典翻译）
- [x] 8.2 `src/config/menu.ts` 注册用户管理菜单项

## 9. 前端：字典注册

- [x] 9.1 `src/dict/index.ts` 新增 `gender: { code: 'gender', valueType: 'STRING' }`

## 10. 验证

- [x] 10.1 运行 `./mvnw test -pl backend` 确认后端测试通过
- [x] 10.2 运行 `pnpm type-check` 和 `pnpm lint` 确认前端无错误
