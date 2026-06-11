# Service

## 基本模式

接口放在 `service/`，实现放在 `service/impl/`。注入 Mapper，通过 `userMapper.xxx()` 直接操作数据库。**禁止** `IService<T>` / `ServiceImpl<M, T>`。

## 返回约定

- Service 只能返回 DTO（common/request/response 子包下的类）
- **禁止** Service 直接返回 Entity 或 VO
- VO 仅用于自定义联表查询结果收集

## 接口模板

```java
/**
 * 用户服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 全量查询用户列表。
     *
     * @param query 查询条件
     * @return 用户列表
     */
    List<UserDTO> list(UserPageRequest query);

    /**
     * 分页查询用户列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResponse<UserDTO> page(UserPageRequest query);

    /**
     * 根据ID查询用户。
     *
     * @param userId 用户ID
     * @return 用户DTO
     */
    UserDTO getById(String userId);

    /**
     * 创建用户。
     *
     * @param request 创建请求
     * @return 创建后的用户DTO
     */
    UserDTO create(@Valid UserCreateRequest request);

    /**
     * 更新用户。
     *
     * @param userId  用户ID
     * @param request 更新请求
     * @return 更新后的用户DTO
     */
    UserDTO update(String userId, @Valid UserUpdateRequest request);

    /**
     * 批量删除用户。
     *
     * @param ids 用户ID列表
     * @return 被删除的用户ID列表
     */
    List<String> delete(List<String> ids);

    /**
     * 根据ID删除用户。
     *
     * @param userId 用户ID
     * @return 被删除的用户ID
     */
    String deleteById(String userId);
}
```

## 实现模板

```java
/**
 * 用户服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
@Validated
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;
    private final UserStructMapper mapper;

    public UserServiceImpl(UserMapper userMapper, UserStructMapper mapper) {
        this.userMapper = userMapper;
        this.mapper = mapper;
    }

    /**
     * 分页查询用户列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<UserDTO> page(UserPageRequest query) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
            .like(StringUtils.hasText(query.getUsername()), SysUser::getUsername, query.getUsername())
            .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
            .orderByDesc(SysUser::getCreatedAt);
        Page<SysUser> page = new Page<>(query.getPage(), query.getSize());
        IPage<SysUser> result = userMapper.selectPage(page, queryWrapper);
        return PageResponse.of(mapper.entityToDTO(result));
    }

    /**
     * 创建用户。
     *
     * @param request 创建请求
     * @return 创建后的用户DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO create(@Valid UserCreateRequest request) {
        SysUser user = mapper.requestToEntity(request);
        userMapper.insert(user);
        return mapper.entityToDTO(user);
    }
}
```

**方法参数不超过 3 个，尽可能少。** 超过时合并为请求对象。

**优先方法级复用，避免代码重复。** 重复出现的逻辑（校验、转换、组装）必须抽取为私有方法，禁止多处复制粘贴。

正例：
```java
// 抽取共用校验
private SysDict getDictOrThrow(String dictId) {
    SysDict dict = dictMapper.selectById(dictId);
    if (dict == null) { throw new OuterException(BizCode.NOT_FOUND, "字典不存在"); }
    return dict;
}
```
反例：`update` 和 `deleteById` 各写一遍 `selectById + null check`。

## 检查清单

- [ ] 接口 + 实现分离
- [ ] 不继承 IService/ServiceImpl，直接注入 Mapper
- [ ] 返回 DTO，不返回 Entity 或 VO
- [ ] 类 + 方法 Javadoc 完整（含 @author）
- [ ] 构造器注入，无 @Autowired
- [ ] @Validated 在类上，@Valid 在方法参数上
- [ ] 读方法无 @Transactional，写方法有 @Transactional(rollbackFor = Exception.class)
- [ ] 方法命名符合规范表
- [ ] create/update 返回实体 DTO，delete 返回被删 id
- [ ] 不出现缩写变量名
