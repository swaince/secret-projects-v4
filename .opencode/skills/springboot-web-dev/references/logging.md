# 日志

使用 SLF4J，禁止 Lombok `@Slf4j`。

```java
private static final Logger LOGGER = LoggerFactory.getLogger(XxxServiceImpl.class);

LOGGER.info("creating user: {}", request.getUsername());
LOGGER.warn("user not found: userId={}", userId);
LOGGER.error("failed to create user", e);
```

- 占位符 `{}` 而非字符串拼接
- 禁止 `System.out.println`
- 禁止 `e.printStackTrace()`

## 检查清单

- [ ] Logger 名称为 LOGGER（全大写）
- [ ] 使用 SLF4J，无 Lombok @Slf4j
- [ ] 占位符 {} 而非字符串拼接
- [ ] 无 System.out / printStackTrace
