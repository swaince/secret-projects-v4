# 测试

测试 Service 层，H2 代替 PostgreSQL。构造器注入，禁止 `@Autowired`。

## 测试配置

`src/test/resources/application.yaml`：
```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
  flyway:
    enabled: false
```

## 测试模板

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceTest {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserServiceTest(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @BeforeEach
    void setUp() {
        userMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldCreateUser() {
        UserCreateRequest request = new UserCreateRequest();
        // set fields ...

        userService.create(request);

        List<SysUser> list = userMapper.selectList(null);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getUsername()).isEqualTo("test");
    }
}
```

- 用 AssertJ：`assertThat(...).hasSize(...).extracting(...)`
- `@BeforeEach` 清理数据
- 测试数据通过 Mapper 直接插入
- 测试环境禁用 Flyway

## TDD 方法论

强制测试驱动开发。没有测试，不写业务代码。

```
RED → GREEN → REFACTOR
```

**铁律：** 测试先行，RED 后才写实现，测试通过前不提交。每方法至少一个用例。

**禁止：** "太简单不需要测试"、"手动测过了"。

## 检查清单

- [ ] 测试 Service 层，非 Controller
- [ ] 构造器注入，无 @Autowired
- [ ] H2 代替 PostgreSQL
- [ ] @BeforeEach 清理数据
- [ ] 使用 AssertJ 断言
- [ ] 先写测试（RED），后写实现（GREEN）
- [ ] 测试通过前不提交
- [ ] 每个 Service 方法至少一个测试用例
