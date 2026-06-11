# @TokenParam

从请求 Token 中解析字段值，用法与 `@RequestParam` 完全一致。

## 组件

```
common/
├── annotation/
│   └── TokenParam.java            # @TokenParam 注解
├── resolver/
│   ├── TokenResolver.java         # 解析器接口（可扩展）
│   ├── UuidTokenResolver.java     # 默认实现
│   └── TokenArgumentResolver.java # Spring 参数解析器
└── config/
    └── WebMvcConfig.java          # 注册解析器
```

## 用法

```java
@PostMapping
public R<DictDTO> create(@TokenParam("userId") String userId,
                         @Valid @RequestBody DictDTO request) {
    return R.ok(dictService.create(request, userId));
}
```

- 从 `Authorization: Bearer xxx` 头或 `?token=xxx` 参数获取 Token
- 默认使用 `UuidTokenResolver`，Token 即为标识符
- 扩展方式：实现 `TokenResolver` 接口，替换 `WebMvcConfig` 中的注册

## 检查清单

- [ ] `@TokenParam` 注解已创建
- [ ] `TokenResolver` 接口已创建
- [ ] `UuidTokenResolver` 默认实现已创建
- [ ] `TokenArgumentResolver` 已创建
- [ ] `WebMvcConfig` 中已注册
- [ ] Controller 使用 `@TokenParam` 获取用户，不在 Service 中写死
- [ ] PMD 通过
