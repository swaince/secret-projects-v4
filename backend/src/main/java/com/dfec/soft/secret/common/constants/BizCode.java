package com.dfec.soft.secret.common.constants;

/**
 * 业务状态码。
 *
 * @author zhangth
 * @since 1.0.0
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
