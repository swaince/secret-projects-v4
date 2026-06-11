---
name: SpringBoot Web Development
description: Use when creating Controller, Service, Mapper, Entity, DTO, VO in this Spring Boot 3.5 + MyBatis-Plus 3.5 + Java 17 + PostgreSQL project. Use when writing REST APIs, CRUD endpoints, Flyway migrations, H2 tests, MapStruct converters, exception handlers, or implementing TDD.
---

# SpringBoot Web Development

Spring Boot 3.5.14 · Java 17 · MyBatis-Plus 3.5 · PostgreSQL · Redis · Quartz · Flyway · MapStruct

**通用要求：能使用枚举或字典的地方，必须使用枚举或字典，禁止硬编码魔法值。算法、设计模式、性能优化必须包含文档注释说明设计意图与选择理由。**

## 模块

| 模块 | 文件 | 说明 |
|------|------|------|
| 项目结构 | [references/project-structure.md](references/project-structure.md) | 多模块分层、包结构、命名规范、DTO 分层、开发流程 |
| 接口规范 | [references/api-conventions.md](references/api-conventions.md) | R\<T\> 响应、PageResponse、URL 约定、方法命名 |
| Controller | [references/controller.md](references/controller.md) | 职责边界、代码模板 |
| Service | [references/service.md](references/service.md) | 接口+实现、返回约定、方法命名规范表 |
| Mapper / ORM | [references/mapper-orm.md](references/mapper-orm.md) | MyBatis-Plus、Lambda 查询、分页、注解、XML |
| MapStruct | [references/mapstruct.md](references/mapstruct.md) | 命名规范、转换模式 |
| 事务 | [references/transaction.md](references/transaction.md) | @Transactional 标准用法 |
| 异常 | [references/exception.md](references/exception.md) | BizCode、异常层次、全局处理器 |
| Web 配置 | [references/web-config.md](references/web-config.md) | UTF-8 编码 |
| @TokenParam | [references/token-param.md](references/token-param.md) | Token 参数解析 |
| UidService | [references/uid-service.md](references/uid-service.md) | UUID v7 生成器 |
| 日志 | [references/logging.md](references/logging.md) | SLF4J + LOGGER |
| 测试 | [references/testing.md](references/testing.md) | H2、TDD 方法论、测试模板 |
| Flyway | [references/flyway.md](references/flyway.md) | 命名规范、标准 SQL、COMMENT ON |
| 分页排序 | [references/pagination.md](references/pagination.md) | QueryRequest、PageResponse |
| 参数校验 | [references/validation.md](references/validation.md) | @Valid、分组校验、嵌套校验 |
| 字典 | [references/dictionary.md](references/dictionary.md) | DictionaryElement、@Dictionary、内置枚举 |
| Entity / DTO | [references/entity-dto.md](references/entity-dto.md) | Entity 审计字段、DTO 分层复用 |
| Javadoc | [references/javadoc.md](references/javadoc.md) | 类/方法/字段注释规范 |
| 禁止事项 | [references/forbidden.md](references/forbidden.md) | 完整禁止清单 |

## Commands

```bash
./mvnw spring-boot:run -pl backend          # dev server :8080
./mvnw test -pl backend                      # all tests
./mvnw test -pl backend -Dtest=UserServiceTest  # single test
./mvnw compile -pl backend                   # MapStruct + PMD
```
