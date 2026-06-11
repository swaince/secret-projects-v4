# Javadoc

## 规范

### 类级别

- 所有类包含类级 Javadoc，**必须含 @author**
- Controller、Service、Mapper、Entity、DTO、VO、Exception、Enum 无一例外

### 方法级别

- Controller 和 Service 的每个方法包含 Javadoc（@param + @return）
- 接口方法的 @param 和 @return 不可省略
- PMD 强制检查：接口抽象方法必须有 Javadoc

### 字段级别

- Entity、DTO、VO 的每个字段包含 Javadoc
- 格式：`/** 字段含义。 */`，多行格式，句末加句号
- 审计字段（createdAt/createdBy/status 等）也不例外

### 格式规则

- 禁止单行 Javadoc（`/** xxx */`），必须使用标准多行格式
- 类 Javadoc 结构：`描述。` + 空行 + `@author` + `@since`
- 方法 Javadoc 结构：`描述。` + 空行 + `@param` + `@return`
- 字段 Javadoc 结构：`字段含义。`（独占多行）

## 模板

### 类

```java
/**
 * 用户服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {
```

### 方法

```java
    /**
     * 分页查询用户列表。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<UserDTO> page(UserPageRequest query) { ... }
```

### 字段

```java
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
```

## 内容验证规则

| 检查项 | 正确示例 | 错误示例 |
|--------|---------|---------|
| 描述有意义 | `创建时间。` | `createdAt`、空注释 |
| 句末加句号 | `用户名。` | `用户名` |
| 不重复字段名 | `创建时间。` | `createdAt 创建时间。` |
| @param 不空 | `@param query 查询条件` | `@param query` |
| @return 不空 | `@return 分页结果` | `@return` |

## 自动检查

```bash
# 检查是否所有类都有 @author
grep -rL "@author" backend/src/main/java/ --include="*.java"

# 检查是否所有字段都有注释（审计字段需单独检查）
grep -r "private.*;" backend/src/main/java/ --include="*.java" | grep -v "//" | grep -v "/\*\*"

# PMD 会在 compile 阶段检查接口方法 Javadoc
./mvnw compile -pl backend
```

## 常见遗漏

| 遗漏位置 | 示例 |
|---------|------|
| Entity 字段 | `private String dictId;` 缺 `/** 字典ID。 */` |
| DTO 字段 | `private String dictName;` 缺注释 |
| 审计字段 | `createdAt` / `createdBy` / `status` 也必须有注释 |
| 接口方法 | `DictionaryElement.getCode()` 缺 @param/@return |
| @author | 任何类都不可缺少 |

## 检查清单

- [ ] 所有类含类级 Javadoc + @author + @since
- [ ] Controller/Service 每方法含 @param + @return（描述不空）
- [ ] Entity/DTO/VO 每字段含 Javadoc（含审计字段）
- [ ] 无单行 Javadoc（`/** xxx */`），所有注释必须为多行格式（含字段注释）
- [ ] 字段注释格式：`/** + 换行 +  * xxx。+ 换行 +  */`（禁止 `/** xxx。 */` 单行）
- [ ] 描述有实际意义，句末有句号
- [ ] 算法/设计模式/优化含设计意图注释
- [ ] PMD compile 阶段无 Javadoc 违规
