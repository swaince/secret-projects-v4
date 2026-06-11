# 项目结构

多业务模块分层架构。通用业务放 `common`，业务模块按一级模块分包。

```
com.dfec.soft.secret
├── common/                    # 通用层（跨模块共享）
│   ├── controller/            # 公共控制器（如健康检查）
│   ├── service/               # 公共服务
│   ├── entity/                # 公共实体（如字典、参数）
│   ├── mapper/                # 公共 Mapper
│   ├── dto/                   # 公共 DTO
│   └── exception/             # 全局异常定义
├── system/                    # 系统管理模块
│   ├── controller/            # UserController, RoleController
│   ├── service/               # UserService, UserServiceImpl
│   ├── mapper/                # UserMapper, RoleMapper
│   ├── entity/                # SysUser, SysRole
│   ├── dto/
│   │   ├── common/            # UserDTO（与 Entity 一对一）
│   │   ├── request/           # QueryRequest(基类), UserCreateRequest
│   │   └── response/          # UserDetailResponse
│   └── mapstruct/             # UserStructMapper
├── content/                   # 内容管理模块
│   └── ...
└── config/                    # 全局 @Configuration
    ├── MybatisPlusConfig.java
    ├── GlobalExceptionHandler.java
    ├── WebMvcConfig.java        # UTF-8 编码
    └── HealthController.java    # 内置健康检查

backend/src/main/resources/
├── db/migration/              # Flyway
├── mappers/                   # MyBatis XML：{module}/XxxMapper.xml
└── application.yaml

backend/src/test/java/com/dfec/soft/secret/
├── system/
│   └── service/UserServiceTest.java
└── common/
    └── service/DictServiceTest.java
```

## 命名规范

| 类型 | 命名 | 示例 |
|------|------|------|
| Controller | `{Entity}Controller` | `UserController` |
| Service 接口 | `{Entity}Service` | `UserService` |
| Service 实现 | `{Entity}ServiceImpl` | `UserServiceImpl` |
| Mapper | `{Entity}Mapper` | `UserMapper` |
| Entity | `{TableName}` 转驼峰 | `SysUser` |
| DTO (common) | `{Entity}DTO` | `UserDTO` |
| Request | `{Entity}{Action}Request` | `UserCreateRequest` |
| Response | `{Entity}{Action}Response` | `UserDetailResponse` |
| VO | `{Entity}VO` | `UserVO`（仅联表查询结果） |
| Query（基类） | `QueryRequest` | 分页排序通用父类 |
| MapStruct | `{Entity}StructMapper` | `UserStructMapper` |
| Exception | `{Desc}Exception` | `OuterException`, `InnerException` |

## DTO 分层

```
dto/
├── common/     # UserDTO，与 Entity 字段一对一映射。入参出参优先使用。
├── request/    # UserCreateRequest，入参专用（可继承 common）
└── response/   # UserDetailResponse，出参扩展（可继承 common）
```

**DTO 复用第一原则：**
- 入参优先用 common DTO + 分组校验，不可行时才创建 request/
- 出参优先用 common DTO，需扩展字段时才 extends 创建 response/
- request 和 response 可 extends common DTO，仅限 dto 包内部，禁止 DTO 继承 Entity
- 禁止为每个接口单独创建仅有细微差异的 DTO

## 注解顺序

除参数注解外，所有注解独占一行：

```java
@RestController
@RequestMapping("/users")
public class UserController { }
```

## 开发流程

1. 创建模块包
2. Flyway 迁移
3. Entity
4. DTO / Request / Response
5. Mapper
6. MapStruct
7. Service
8. Controller
9. 测试（TDD 先行）
10. 编译验证（`./mvnw compile`）

## 内置健康检查

```java
/**
 * 健康检查控制器（内置，不可删除）。
 *
 * @author zhangth
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public R<String> checkHealth() {
        return R.ok("OK");
    }
}
```

## 检查清单

- [ ] 包结构归属正确模块（common / 业务模块）
- [ ] 命名符合规范表
- [ ] DTO 优先复用，request/response 仅在必要时创建
- [ ] 注解独占一行（参数注解除外）
- [ ] 按开发流程 10 步顺序执行
