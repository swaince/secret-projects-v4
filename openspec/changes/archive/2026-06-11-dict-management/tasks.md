## 1. 数据库迁移

- [x] 1.1 创建 Flyway 迁移脚本 `V1.0.0.1__create_dict_table.sql`（sys_dict + sys_dict_item DDL，含 COMMENT ON）
- [x] 1.2 运行 `./mvnw flyway:migrate -pl backend` 确认迁移成功

## 2. Entity + DTO（后端基础设施）

- [x] 2.1 创建 `system/entity/SysDict.java`（含 @TableField + 审计字段 + Javadoc）
- [x] 2.2 创建 `system/entity/SysDictItem.java`（含 @TableField + 审计字段 + Javadoc）
- [x] 2.3 创建 `system/dto/common/DictDTO.java`（类 + 字段 Javadoc，含 Create/Update 校验分组）
- [x] 2.4 创建 `system/dto/common/DictItemDTO.java`（类 + 字段 Javadoc，含 Create/Update 校验分组）
- [x] 2.5 使用 DictDTO + 校验分组替代独立 Request 类（Create.class / Update.class）
- [x] 2.6 使用 DictItemDTO + 校验分组替代独立 Request 类
- [x] 2.7 创建 `system/dto/request/DictPageRequest.java` extends QueryRequest

## 3. Mapper + MapStruct（后端）

- [x] 3.1 创建 `system/mapper/DictMapper.java` extends BaseMapper\<SysDict\>
- [x] 3.2 创建 `system/mapper/DictItemMapper.java` extends BaseMapper\<SysDictItem\>
- [x] 3.3 创建 `system/mapstruct/DictStructMapper.java`（entityToDTO, requestToEntity, updateEntity）
- [x] 3.4 创建 `system/mapstruct/DictItemStructMapper.java`（同上）

## 4. DictService（TDD 先行）

- [x] 4.1 编写 `DictServiceTest.java` 测试用例（分页/新增/修改/删除/批量删/编码唯一）
- [x] 4.2 运行 `./mvnw test -pl backend -Dtest=DictServiceTest` 确认 RED
- [x] 4.3 创建 `system/service/DictService.java` 接口（含方法 Javadoc）
- [x] 4.4 创建 `system/service/impl/DictServiceImpl.java` 实现
- [x] 4.5 运行 `./mvnw test -pl backend -Dtest=DictServiceTest` 确认 GREEN

## 5. DictItemService（TDD 先行）

- [x] 5.1 编写 `DictItemServiceTest.java` 测试用例（新增/修改/删除/批量删/按dictId删/按dictCode删/查询列表）
- [x] 5.2 运行 `./mvnw test -pl backend -Dtest=DictItemServiceTest` 确认 RED
- [x] 5.3 创建 `system/service/DictItemService.java` 接口（含方法 Javadoc）
- [x] 5.4 创建 `system/service/impl/DictItemServiceImpl.java` 实现
- [x] 5.5 运行 `./mvnw test -pl backend -Dtest=DictItemServiceTest` 确认 GREEN

## 6. DictController（TDD 先行）

- [x] 6.1 编写 `DictControllerTest.java` 测试用例（分页/新增/修改/删除/批量删）
- [x] 6.2 运行 `./mvnw test -pl backend -Dtest=DictControllerTest` 确认 RED
- [x] 6.3 创建 `system/controller/DictController.java`（含类 + 方法 Javadoc）
- [x] 6.4 运行 `./mvnw test -pl backend -Dtest=DictControllerTest` 确认 GREEN

## 7. DictItemController（TDD 先行）

- [x] 7.1 编写 `DictItemControllerTest.java` 测试用例（查询列表/新增/修改/删除/批量删/按dictId删/按dictCode删）
- [x] 7.2 运行 `./mvnw test -pl backend -Dtest=DictItemControllerTest` 确认 RED
- [x] 7.3 创建 `system/controller/DictItemController.java`（含类 + 方法 Javadoc）
- [x] 7.4 运行 `./mvnw test -pl backend -Dtest=DictItemControllerTest` 确认 GREEN

## 8. 前端字典管理页面（TDD 先行）

- [x] 8.1 编写 `DictIndex.spec.ts` 测试用例（列表渲染/搜索/新增/删除抽屉）
- [x] 8.2 运行 `pnpm test:unit -- DictIndex.spec.ts` 确认 RED
- [x] 8.3 创建 `views/system/dict/Index.vue`（分页表格 + shadcn-vue Table）
- [x] 8.4 创建 `views/system/dict/components/DictItemDrawer.vue`（抽屉 + 字典项列表 + 批量删除）
- [x] 8.5 添加路由 `/system/dict` → DictIndex（menu.ts 增加 dict-list 条目 + component 字段支持）
- [x] 8.6 运行 `pnpm test:unit -- DictIndex.spec.ts` 确认 GREEN

## 9. 前后端联调验证

- [x] 9.1 运行 `./mvnw test -pl backend` 确认全部后端测试通过
- [x] 9.2 运行 `./mvnw compile -pl backend` 确认 MapStruct 生成 + PMD 通过
- [x] 9.3 运行 `pnpm test:unit` 确认全部前端测试通过
- [x] 9.4 运行 `pnpm type-check && pnpm lint && pnpm build` 确认前端构建成功
