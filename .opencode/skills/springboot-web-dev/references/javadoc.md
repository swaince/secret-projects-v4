# Javadoc

## 规范

- 所有类包含类级 Javadoc（Controller、Service、Mapper、Entity、DTO、VO），**必须含 @author**
- Controller 和 Service 的每个方法包含 Javadoc（@param + @return）
- Entity、DTO、VO 的每个字段包含 Javadoc
- 禁止单行 Javadoc（`/** xxx */`），必须使用标准多行格式

## 模板

```java
/**
 * 用户服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 分页查询用户列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<UserDTO> page(UserPageRequest query) { ... }
}
```

```java
/**
 * 用户实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_user")
public class SysUser {
    /**
     * 用户ID。
     */
    @TableId(type = IdType.INPUT)
    private String userId;

    /**
     * 用户名。
     */
    @TableField("username")
    private String username;
}
```

## 检查清单

- [ ] 所有类含类级 Javadoc + @author
- [ ] Controller/Service 每方法含 @param + @return
- [ ] Entity/DTO/VO 每字段含 Javadoc
- [ ] 无单行 Javadoc（/** xxx */）
- [ ] 算法/设计模式/优化含设计意图注释
