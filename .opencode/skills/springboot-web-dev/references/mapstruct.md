# MapStruct

编译期生成转换代码（`target/generated-sources/`）。官方文档：https://mapstruct.org/documentation/dev/reference/html/

## 命名规范

`{source}To{target}`，单实体与集合使用重载。

```java
/**
 * 用户 StructMapper。
 *
 * @author zhangth
 */
@Mapper(componentModel = "spring")
public interface UserStructMapper {
    // entity → dto
    UserDTO entityToDTO(SysUser entity);
    List<UserDTO> entityToDTO(List<SysUser> list);
    IPage<UserDTO> entityToDTO(IPage<SysUser> page);

    // request → entity
    SysUser requestToEntity(UserCreateRequest request);

    // 更新
    void updateEntity(@MappingTarget SysUser entity, UserUpdateRequest request);
}
```

## 检查清单

- [ ] `componentModel = "spring"` 必须指定
- [ ] 方法命名 `{source}To{target}`
- [ ] 单实体与集合同名重载
- [ ] @MappingTarget 用于更新方法
- [ ] 生成时机：`./mvnw compile`
