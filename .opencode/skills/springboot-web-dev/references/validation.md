# 参数校验

**校验注解必须提供 `message` 属性，禁止省略。**

## 基础校验

```java
/**
 * 用户创建请求。
 *
 * @author zhangth
 */
public class UserCreateRequest {
    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度为 2-50 个字符")
    private String username;

    /** 邮箱 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 状态 1-启用 0-禁用 */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值最小为 0")
    @Max(value = 1, message = "状态值最大为 1")
    private Integer status;
}
```

## 分组校验

```java
public interface Create {}
public interface Update {}

/**
 * 用户请求（分组校验示例）。
 *
 * @author zhangth
 */
public class UserRequest {
    /** 用户ID（仅更新时校验） */
    @NotNull(groups = Update.class, message = "用户ID不能为空")
    private String userId;

    /** 用户名 */
    @NotBlank(groups = {Create.class, Update.class}, message = "用户名不能为空")
    private String username;
}

@PostMapping
public R<UserDTO> create(@Validated(Create.class) @RequestBody UserRequest request) { }

@PutMapping("/{userId}")
public R<UserDTO> update(@Validated(Update.class) @RequestBody UserRequest request) { }
```

## 嵌套校验

子对象需要 `@Valid` 触发级联校验：

```java
/**
 * 订单创建请求。
 *
 * @author zhangth
 */
public class OrderCreateRequest {
    /** 订单号 */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /** 订单项列表（嵌套校验） */
    @Valid
    @NotNull(message = "订单项不能为空")
    private List<OrderItemRequest> items;
}

/**
 * 订单项请求。
 *
 * @author zhangth
 */
public class OrderItemRequest {
    /** 商品ID */
    @NotNull(message = "商品ID不能为空")
    private String productId;

    /** 数量 */
    @Min(value = 1, message = "数量最小为 1")
    private Integer quantity;
}
```

Controller + Service 双重校验。

### 标准校验（接口 + 实现双向声明）

```java
// 接口
UserDTO create(@Valid UserCreateRequest request);

// 实现
@Override @Validated
public UserDTO create(@Valid UserCreateRequest request) { ... }
```

### 分组校验（接口声明分组 + 实现配合 @Validated）

```java
/**
 * 字典项服务接口。
 */
public interface DictionaryItemService {

    /**
     * 新增字典项。
     *
     * @param dictionaryItem 字典项
     * @return 创建后的字典项
     */
    @Validated(Creating.class)
    DictionaryItemDTO create(@Valid DictionaryItemDTO dictionaryItem);

    /**
     * 更新字典项。
     *
     * @param dictionaryItem 字典项信息
     * @return 更新后的字典项
     */
    @Validated(Updating.class)
    DictionaryItemDTO update(@Valid DictionaryItemDTO dictionaryItem);
}

/**
 * 字典项服务实现。
 */
@Service
@Validated
public class DictionaryItemServiceImpl implements DictionaryItemService {

    @Override
    public DictionaryItemDTO create(@Valid DictionaryItemDTO dictionaryItem) { ... }

    @Override
    public DictionaryItemDTO update(@Valid DictionaryItemDTO dictionaryItem) { ... }
}
```

- 接口声明 `@Validated(Group.class)` 定义校验契约
- 实现类加 `@Validated` 触发校验执行
- 方法参数同时加 `@Valid`，双重保障

## 检查清单

- [ ] 所有校验注解包含 message 属性
- [ ] Controller + Service 双重 @Valid
- [ ] 分组校验使用 @Validated(Group.class)
- [ ] 嵌套对象加 @Valid
- [ ] 字段含 Javadoc
