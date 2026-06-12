## 1. 数据库迁移

- [x] 1.1 创建 Flyway `V1.0.0.5__create_dept_table.sql`（建表 + 列注释）

## 2. 后端数据层

- [x] 2.1 创建 Entity `SysDept.java`（含 parentId 字段）
- [x] 2.2 创建 Mapper `DeptMapper.java`
- [x] 2.3 创建 DTO `DeptDTO.java`（含 children: List<DeptDTO> + parentId）
- [x] 2.4 创建 MapStruct `DeptStructMapper.java`（含 @Mapping ignore）

## 3. 后端 Service 层

- [x] 3.1 编写 `DeptService` 接口（tree、getById、create、update、delete、deleteById）
- [x] 3.2 编写 `DeptServiceImpl` 单元测试
- [x] 3.3 实现 `DeptServiceImpl`（树构建 + 子部门删除保护 + 内置保护）

## 4. 后端 Controller 层

- [x] 4.1 编写 `DeptController` 集成测试
- [x] 4.2 实现 `DeptController`

## 5. 前端

- [x] 5.1 创建 `api/dept.ts`
- [x] 5.2 创建 `views/system/DeptView.vue`（树形视图 + Dialog）
- [x] 5.3 更新 `config/menu.ts`（dept-list 移至二级菜单 + component）

## 6. 验证

- [x] 6.1 运行后端测试
- [x] 6.2 运行前端 type-check + lint
