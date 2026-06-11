# UidService

UUID v7（时间有序）ID 生成服务。依赖 `java-uuid-generator` 库。

## 组件

```
common/service/
├── UidService.java                  # 接口
└── impl/
    └── Uuidv7UidServiceImpl.java    # 实现，@Service 自动注入
```

## 用法

```java
@Service
public class UserServiceImpl implements UserService {

    private final UidService uidService;

    public UserServiceImpl(UserMapper userMapper, UidService uidService) {
        this.userMapper = userMapper;
        this.uidService = uidService;
    }

    @Override
    public UserDTO create(UserCreateRequest request) {
        User user = mapper.requestToEntity(request);
        user.setUserId(uidService.next());
        userMapper.insert(user);
        return mapper.entityToDTO(user);
    }
}
```

## 检查清单

- [ ] `UidService` 接口已创建
- [ ] `Uuidv7UidServiceImpl` 已创建，@Service 注解
- [ ] 使用 `java-uuid-generator` 的 `Generators.timeBasedEpochGenerator()`
- [ ] 所有 create 方法使用 `uidService.next()` 生成 ID，**禁止 `UUID.randomUUID()`**
- [ ] 系统内所有 ID 生成必须通过 `UidService`，测试代码除外
