# Entity / DTO

## Entity 规范

```java
/**
 * 用户实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_user")
public class SysUser {
    /** 用户ID */
    @TableId(type = IdType.INPUT)
    private String userId;

    /** 用户名 */
    @TableField("username")
    private String username;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 创建时间 */
    @TableField(value = "create_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 创建人 */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createdBy;

    /** 更新时间 */
    @TableField(value = "update_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 更新人 */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /** 状态 1-启用 0-禁用 */
    @TableField("status")
    private Integer status;

    /** 是否内置 1-是 0-否 */
    @TableField("built_in")
    private Integer builtIn;

    /** 是否删除 1-是 0-否 */
    @TableField("deleted")
    private Integer deleted;

    /** 备注 */
    @TableField("remark")
    private String remark;
}
```

### 规则

- 每个字段显式 @TableField，禁止省略（主键仅 @TableId）
- 审计字段禁止 `@TableField(fill = FieldFill.INSERT/INSERT_UPDATE)`，应用层手动设置
- 禁止 boolean/BOOLEAN，统一 Integer(0/1)
- 使用 LocalDateTime，不用 Date
- 主键 IdType.INPUT，应用层生成 UUID v7
- 所有实体必须包含审计字段：create_at/create_by/update_at/update_by/status/built_in/deleted/remark

## DTO 规范

```java
/**
 * 用户DTO。
 *
 * @author zhangth
 */
public class UserDTO {
    /** 用户ID */
    private String userId;
    /** 用户名 */
    private String username;
}

/**
 * 用户创建请求。
 *
 * @author zhangth
 */
public class UserCreateRequest extends UserDTO {
    // 可添加额外校验注解
}

/**
 * 用户详情响应。
 *
 * @author zhangth
 */
public class UserDetailResponse extends UserDTO {
    /** 部门名称 */
    private String deptName;
}
```

### 规则

- DTO 复用第一原则：优先 common DTO，不满足再创建 request/response
- request/response 可 extends common DTO，仅限 dto 包内部
- 禁止 DTO 继承 Entity

### 创建前强制检查

创建 request/response 之前必须自问，全部为"否"才能创建：

1. 现有 common DTO 字段是否覆盖所有入参？ → 能覆盖则直接复用
2. 能否通过分组校验（`@Validated(Group.class)`）区分场景？ → 能则复用
3. 新类字段是否与 common DTO 完全一致？ → **禁止创建**
4. common DTO 多出的字段（如 id、createdAt）能否由 Service 生成覆盖？ → 能则复用，传 null 即可

**核心原则：宁可 DTO 有冗余 null 字段，不可因"字段不完全匹配"而新建 Request。**

## 检查清单

- [ ] 每个字段显式 @TableField
- [ ] 主键 IdType.INPUT + UUID v7
- [ ] 无 boolean 类型，统一 Integer
- [ ] 审计字段 8 个齐全
- [ ] 类 + 字段 Javadoc 完整
- [ ] DTO 优先复用，request/response 在必要时创建
- [ ] DTO 不继承 Entity
