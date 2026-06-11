# Controller

## 职责边界

```
请求 → 参数校验 → 调用 Service → 返回 R
                    ↑
              不得越权处理业务
```

| 允许 | 禁止 |
|------|------|
| 接收参数并校验 (`@Valid`) | 编写业务逻辑 |
| 调用 Service 方法 | 直接操作 Mapper |
| 组装 R 返回 | 处理事务 |
| 简单参数转换 | 复杂数据组装（应放在 Service） |
| — | try-catch 异常（由 GlobalExceptionHandler 统一处理） |

## 代码模板

```java
/**
 * 用户控制器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping
    public R<PageResponse<UserDTO>> list(UserPageRequest query) {
        return R.ok(userService.page(query));
    }

    /**
     * 根据ID查询用户。
     *
     * @param userId 用户ID
     * @return 用户DTO
     */
    @GetMapping("/{userId}")
    public R<UserDTO> getById(@PathVariable String userId) {
        return R.ok(userService.getById(userId));
    }

    /**
     * 创建用户。
     *
     * @param request 创建请求
     * @return 创建后的用户DTO
     */
    @PostMapping
    public R<UserDTO> create(@Valid @RequestBody UserCreateRequest request) {
        return R.ok(userService.create(request));
    }

    /**
     * 更新用户。
     *
     * @param userId  用户ID
     * @param request 更新请求
     * @return 更新后的用户DTO
     */
    @PutMapping("/{userId}")
    public R<UserDTO> update(@PathVariable String userId,
                             @Valid @RequestBody UserUpdateRequest request) {
        return R.ok(userService.update(userId, request));
    }

    /**
     * 批量删除用户。
     *
     * @param ids 用户ID列表
     * @return 被删除的用户ID列表
     */
    @DeleteMapping
    public R<List<String>> delete(@RequestBody @NotEmpty List<String> ids) {
        return R.ok(userService.delete(ids));
    }

    /**
     * 根据ID删除用户。
     *
     * @param userId 用户ID
     * @return 被删除的用户ID
     */
    @DeleteMapping("/{userId}")
    public R<String> deleteById(@PathVariable String userId) {
        return R.ok(userService.deleteById(userId));
    }
}
```

Controller 方法与 Service 统一命名。**方法参数不超过 3 个，尽可能少。**

## 检查清单

- [ ] 类级别添加 Javadoc（含 @author）
- [ ] 每个方法添加 Javadoc（@param + @return）
- [ ] 构造器注入，无 @Autowired
- [ ] 只做参数接收、调用 Service、返回 R
- [ ] 不写业务逻辑、不调 Mapper、不用 try-catch
- [ ] 方法命名与 Service 一致
- [ ] create/update 返回实体 DTO，delete 返回被删 id
- [ ] 路径参数显式命名
