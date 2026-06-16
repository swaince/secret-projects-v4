## 1. 后端：数据库

- [x] 1.1 创建 Flyway 迁移建 `sys_user_relation` 和 `sys_authorization` 表 + 索引

## 2. 后端：枚举

- [x] 2.1 创建 `SubjectType` 枚举（@Dictionary: USER/DEPT/POST/ROLE）
- [x] 2.2 创建 `RelationType` 枚举（@Dictionary: DEPT/POST/ROLE）

## 3. 后端：实体 + DTO + Mapper

- [x] 3.1 创建 `SysUserRelation` 和 `SysAuthorization` 实体
- [x] 3.2 创建 `UserRelationMapper` 和 `AuthorizationMapper`
- [x] 3.3 创建关系保存/查询 DTO

## 4. 后端：Service

- [x] 4.1 UserRelationService/Impl — saveBatch, getByUser
- [x] 4.2 AuthorizationService/Impl — saveBatch, getBySubject

## 5. 后端：Controller

- [x] 5.1 UserRelationController — POST /relations/{type}, GET /relations/{type}/{userId}
- [x] 5.2 AuthController — POST /authorizations/{type}, GET /authorizations/{type}/{subjectId}

## 6. 后端：测试

- [x] 6.1 编写关系/授权保存/查询集成测试

## 7. 前端：API 层

- [x] 7.1 创建 `src/api/auth.ts`

## 8. 前端：页面

- [x] 8.1 创建 `UserAuthView.vue`（4个Tab: 菜单/部门/岗位/角色）
- [x] 8.2 创建 `DeptAuthView.vue`（部门菜单授权）
- [x] 8.3 创建 `PostAuthView.vue`（岗位菜单授权）
- [x] 8.4 创建 `RoleAuthView.vue`（角色菜单授权）

## 9. 菜单数据 + 字典

- [x] 9.1 Flyway 迁移注册授权管理菜单项 + 字典注册

## 10. 验证

- [x] 10.1 `./mvnw test -pl backend`
- [x] 10.2 `pnpm type-check` + `pnpm lint`
