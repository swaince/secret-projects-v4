## 1. 数据库迁移

- [x] 1.1 创建 Flyway 脚本 `V1.0.0.2__add_dict_item_label.sql`，为 `sys_dict_item` 新增 `item_label VARCHAR(100) DEFAULT '' NOT NULL` 列
- [x] 1.2 验证迁移在 H2（测试）和 PostgreSQL（开发）均可执行

## 2. 实体与 DTO 适配

- [x] 2.1 `SysDictItem` 新增 `itemLabel` 字段（`@TableField("item_label")`）+ getter/setter
- [x] 2.2 `DictItemDTO` 新增 `itemLabel` 字段
- [x] 2.3 适配现有 `DictItemServiceTest` 和 `DictItemControllerTest` 中涉及 `DictItemDTO` / `SysDictItem` 的测试数据，补充 `itemLabel`

## 3. DictSyncRunner 测试（TDD：先写测试）

- [x] 3.1 创建 `DictSyncRunnerTest`，测试：扫描发现所有 `@Dictionary` 枚举（当前 5 个）
- [x] 3.2 测试：首次启动后 `sys_dict` 表包含所有内置字典记录（`built_in=1`）
- [x] 3.3 测试：首次启动后 `sys_dict_item` 表包含所有枚举常量对应的记录，`item_key`/`item_label`/`item_value` 映射正确
- [x] 3.4 测试：重复执行同步后数据幂等（记录数不变、内容一致）
- [x] 3.5 测试：业务字典（`built_in=0`）在同步前后不受影响
- [x] 3.6 测试：同步失败时事务回滚（模拟 DB 异常）

## 4. DictSyncRunner 实现

- [x] 4.1 创建 `DictSyncRunner implements ApplicationRunner`，注入 `DictMapper`、`DictItemMapper`、`UidService`
- [x] 4.2 实现 `scanDictionaryEnums()`：使用 `ClassPathScanningCandidateComponentProvider` + `AnnotationTypeFilter(@Dictionary)` 扫描基础包，过滤仅保留实现 `DictionaryElement` 的枚举类
- [x] 4.3 实现 `deleteBuiltinDicts()`：删除 `sys_dict_item WHERE built_in=1`，再删除 `sys_dict WHERE built_in=1`
- [x] 4.4 实现 `insertBuiltinDicts()`：遍历枚举类，按映射规则插入 `sys_dict` 和 `sys_dict_item`
- [x] 4.5 实现 `syncBuiltinDicts()`：`@Transactional` 方法，串联扫描→删除→插入，添加 INFO/ERROR 日志

## 5. 验证

- [x] 5.1 运行 `./mvnw test -pl backend`，全部测试通过
- [x] 5.2 运行 `./mvnw compile -pl backend`，PMD + P3C 静态分析通过
- [x] 5.3 本地启动验证：`./mvnw spring-boot:run -pl backend`，观察日志确认同步成功
