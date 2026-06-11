# Backend

Spring Boot 3.5.14 · Java 17 · MyBatis-Plus 3.5.15 · PostgreSQL · Redis · Quartz · Flyway

## 强制技能

**后端开发必须加载 `springboot-web-dev` 技能指导所有开发工作。** 该技能包含完整的编码规范、架构约定、检查清单（项目结构、接口规范、Controller/Service/Mapper/MapStruct、事务、异常、日志、测试、Flyway、参数校验、字典、Javadoc 等 18 个模块）。

## 入口

- 主类: `com.dfec.soft.secret.SecretApp`
- 基础包: `com.dfec.soft.secret`
- 端口: 8080
- 配置: `src/main/resources/application.yaml`

## 开发命令

```bash
# 仅构建 backend 模块
./mvnw clean package -pl backend -DskipTests

# 启动 backend（开发模式，含 devtools）
./mvnw spring-boot:run -pl backend

# 运行全部测试
./mvnw test -pl backend

# 运行单个测试类
./mvnw test -pl backend -Dtest=MyTestClass
```

## 技术要点

- **ORM**: MyBatis-Plus（不是 JPA），Mapper 接口继承 `BaseMapper<T>`。
- **数据库迁移**: Flyway（PostgreSQL），SQL 迁移脚本放 `src/main/resources/db/migration/`。
- **对象映射**: MapStruct，Mapper 接口定义在 `mapstruct` 包下，编译期生成实现。
- **测试**: H2 内存数据库代替 PostgreSQL，MyBatis-Plus 测试 starter 已引入。
  - **注意**: 目前 `src/test/` 目录尚未创建，测试待编写。
- **静态分析**: PMD + 阿里 P3C 规则，绑定在 `compile` 阶段，`./mvnw compile` 会自动触发 `pmd:check`。
- **XML 资源**: `src/main/java` 下的 `*.xml` 会被编译打包（MyBatis XML Mapper）。
- **YAML 配置**: 配置文件（`*.yaml`, `*.properties`）会经过 Maven resource filtering 处理变量替换。
