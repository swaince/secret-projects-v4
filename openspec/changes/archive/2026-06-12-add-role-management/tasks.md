## 1. 数据库迁移

- [x] 1.1 创建 Flyway 迁移 `V1.0.0.4__create_role_table.sql`（建表 + 列注释）

## 2. 后端数据层

- [x] 2.1 创建 Entity `SysRole.java`
- [x] 2.2 创建 Mapper `RoleMapper.java`
- [x] 2.3 创建 DTO `RoleDTO.java`（含校验）
- [x] 2.4 创建 `RolePageRequest.java`（roleName, roleCode）
- [x] 2.5 创建 MapStruct `RoleStructMapper.java`（含 @Mapping ignore）

## 3. 后端 Service 层

- [x] 3.1 编写 `RoleService` 接口
- [x] 3.2 编写 `RoleServiceImpl` 单元测试
- [x] 3.3 实现 `RoleServiceImpl`

## 4. 后端 Controller 层

- [x] 4.1 编写 `RoleController` 集成测试
- [x] 4.2 实现 `RoleController`

## 5. 前端

- [x] 5.1 创建 `api/role.ts`
- [x] 5.2 创建 `views/system/RoleView.vue`
- [x] 5.3 更新 `config/menu.ts` 添加 component

## 6. 验证

- [x] 6.1 运行后端测试
- [x] 6.2 运行前端 type-check + lint
