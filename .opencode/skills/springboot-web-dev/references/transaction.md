# 事务

## 标准用法

```java
@Transactional(rollbackFor = Exception.class)
public void createWithRole(UserCreateRequest request) {
    SysUser user = mapper.requestToEntity(request);
    userMapper.insert(user);
    roleMapper.insert(new SysRole().setUserId(user.getUserId()));
}
```

- 写操作加 `@Transactional(rollbackFor = Exception.class)`
- **必须显式指定 `rollbackFor`**：默认只回滚 `RuntimeException`
- 反例：裸 `@Transactional`
- 读方法不加事务

## 检查清单

- [ ] @Transactional 显式指定 rollbackFor = Exception.class
- [ ] 无裸 @Transactional
- [ ] 读方法无事务注解
- [ ] 多表写在同一事务方法内
