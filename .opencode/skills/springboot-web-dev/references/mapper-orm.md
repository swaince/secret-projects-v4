# Mapper / ORM

**本项目使用 MyBatis-Plus，不是 JPA。** 禁止 `@Entity`、`@Column`、`@OneToMany`。

## Mapper 接口

```java
/**
 * 用户 Mapper。
 *
 * @author zhangth
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
```

## BaseMapper 常用方法

`insert(T)` · `updateById(T)` · `deleteById(id)` · `selectById(id)` · `selectList(wrapper)` · `selectPage(page, wrapper)` · `selectCount(wrapper)`

## Lambda 查询

```java
LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(SysUser::getUsername, "admin")
    .like(SysUser::getEmail, "@dfec.com")
    .in(SysUser::getStatus, List.of(1, 2))
    .ge(SysUser::getCreatedAt, startDate)
    .orderByDesc(SysUser::getUserId);
List<SysUser> list = userMapper.selectList(queryWrapper);
```

条件构造时用 `StringUtils.hasText()` 做空值保护。

## 分页插件

```java
/**
 * MyBatis-Plus 配置。
 *
 * @author zhangth
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor interceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
```

## Lambda 更新

```java
userMapper.update(null,
    new LambdaUpdateWrapper<SysUser>()
        .set(SysUser::getStatus, 0)
        .eq(SysUser::getUserId, userId));
```

## 注解

```java
@TableName("sys_user")
@TableId(type = IdType.INPUT)   // UUID v7，应用层生成
@TableField("username")         // 字段映射（每个字段必须显式标注）
@TableField(exist = false)      // 非数据库字段
```

**禁止** `@TableLogic`（逻辑删除）和 `@Version`（乐观锁）。
- **禁止 `@TableField(fill = FieldFill.INSERT/INSERT_UPDATE)` 自动填充审计字段**。审计字段由应用层手动设置。

## 通用查询规则

- 所有查询必须排除软删除数据：`queryWrapper.eq(Entity::getDeleted, Deleted.NO.getValue())`
- 内置数据（`builtIn = 1`）除特殊声明外，禁止修改和删除（Service 层校验）

## XML Mapper

放在 `src/main/resources/mappers/{module}/`。

```yaml
mybatis-plus:
  mapper-locations: classpath*:mappers/**/*.xml
```

## 检查清单

- [ ] 使用 MyBatis-Plus，无 JPA 注解
- [ ] 每个字段显式 @TableField 标注映射
- [ ] 主键 @TableId(type = IdType.INPUT)，应用层生成 UUID v7
- [ ] 无 @TableLogic / @Version
- [ ] 审计字段手动设置，无 @TableField(fill = ...)
- [ ] 所有查询添加 deleted = 0 条件
- [ ] 内置数据删除/修改前校验 builtIn
- [ ] 变量名完整（queryWrapper 非 qw）
- [ ] Boolean/boolean 禁止，统一 Integer(0/1)
- [ ] XML Mapper 放在 resources/mappers/{module}/
