# 日志

使用 SLF4J，禁止 Lombok `@Slf4j`。

```java
private static final Logger LOGGER = LoggerFactory.getLogger(XxxServiceImpl.class);

LOGGER.info("creating user: {}", request.getUsername());
LOGGER.warn("user not found: userId={}", userId);
LOGGER.error("failed to create user", e);
```

## 日志级别规范

| 级别 | 场景 | 示例 |
|------|------|------|
| `LOGGER.info` | 写操作（create/update/delete）、查询入口 | `LOGGER.info("创建字典，dictId={}", id)` |
| `LOGGER.warn` | 资源未找到、业务异常 | `LOGGER.warn("字典不存在，dictId={}", id)` |
| `LOGGER.error` | 未预期异常（GlobalExceptionHandler） | `LOGGER.error("inner exception", e)` |

- **禁止** `LOGGER.debug`（生产环境不可见，业务操作应使用 info）
- 占位符 `{}` 而非字符串拼接
- 禁止 `System.out.println`
- 禁止 `e.printStackTrace()`

## 检查清单

- [ ] Logger 名称为 LOGGER（全大写）
- [ ] 使用 SLF4J，无 Lombok @Slf4j
- [ ] 占位符 {} 而非字符串拼接
- [ ] 无 System.out / printStackTrace
- [ ] 写操作使用 LOGGER.info，非 LOGGER.debug
- [ ] 异常在 GlobalExceptionHandler 中统一 LOGGER.error
