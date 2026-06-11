# 异常

## BizCode

```java
/**
 * 业务状态码。
 *
 * @author zhangth
 */
@Dictionary(name = "业务状态码", code = "biz_code")
public enum BizCode implements DictionaryElement<Integer> {
    //@formatter:off
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "数据冲突"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    //@formatter:on
    ;

    private final Integer value;
    private final String message;
    BizCode(Integer value, String message) { this.value = value; this.message = message; }
    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public Integer getValue() { return value; }
}
```

## 异常层次

```
RuntimeException
  └── BusinessException（可实例化，兜底用）
        ├── OuterException          → 对外暴露，可继承
        └── InnerException          → 内部异常基类，可继承
              └── DataConflictException
```

```java
public class BusinessException extends RuntimeException {
    private final BizCode bizCode;
    public BusinessException(BizCode bizCode, String message) { super(message); this.bizCode = bizCode; }
    public BusinessException(BizCode bizCode) { this(bizCode, bizCode.getMessage()); }
    public BizCode getBizCode() { return bizCode; }
}

public class OuterException extends BusinessException {
    public OuterException(BizCode bizCode, String message) { super(bizCode, message); }
    public OuterException(BizCode bizCode) { super(bizCode); }
}

public class InnerException extends BusinessException {
    public InnerException(BizCode bizCode, String message) { super(bizCode, message); }
    public InnerException(BizCode bizCode) { super(bizCode); }
}
```

- 参数校验失败、权限不足、资源不存在 → `OuterException`
- 数据库异常、外部调用失败 → `InnerException`

## GlobalExceptionHandler

```java
/**
 * 全局异常处理器。
 *
 * @author zhangth
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));
        return R.err(BizCode.BAD_REQUEST.getValue(), message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return R.err(BizCode.BAD_REQUEST.getValue(), "请求体格式错误");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return R.err(BizCode.BAD_REQUEST.getValue(), "缺少必要参数: " + e.getParameterName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return R.err(BizCode.BAD_REQUEST.getValue(), "不支持的请求方法: " + e.getMethod());
    }

    @ExceptionHandler(OuterException.class)
    public R<Void> handleOuter(OuterException e) {
        return R.err(e.getBizCode().getValue(), e.getMessage());
    }

    @ExceptionHandler(InnerException.class)
    public R<Void> handleInner(InnerException e) {
        LOGGER.error("inner exception: {}", e.getMessage(), e);
        return R.err(BizCode.INTERNAL_ERROR.getValue(), BizCode.INTERNAL_ERROR.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusiness(BusinessException e) {
        LOGGER.error("business exception: {}", e.getMessage(), e);
        return R.err(e.getBizCode().getValue(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntime(RuntimeException e) {
        LOGGER.error("runtime exception", e);
        return R.err(BizCode.INTERNAL_ERROR.getValue(), BizCode.INTERNAL_ERROR.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        LOGGER.error("exception", e);
        return R.err(BizCode.INTERNAL_ERROR.getValue(), BizCode.INTERNAL_ERROR.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public R<Void> handleThrowable(Throwable e) {
        LOGGER.error("throwable", e);
        return R.err(BizCode.INTERNAL_ERROR.getValue(), BizCode.INTERNAL_ERROR.getMessage());
    }
}
```

捕获顺序：Spring 异常 → OuterException → InnerException → BusinessException → RuntimeException → Exception → Throwable。

## 检查清单

- [ ] BizCode 实现 DictionaryElement，@Dictionary 注解
- [ ] 异常层次正确：OuterException/InnerException 继承 BusinessException
- [ ] InnerException 不返回原始消息
- [ ] 全局处理器覆盖完整捕获链
- [ ] 所有 R.err 使用 BizCode.getValue()
