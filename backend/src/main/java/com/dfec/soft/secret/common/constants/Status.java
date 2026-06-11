package com.dfec.soft.secret.common.constants;

/**
 * 系统状态。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "状态", code = "status")
public enum Status implements DictionaryElement<Integer> {
    //@formatter:off
    ENABLED(1, "启用"),
    DISABLED(0, "禁用"),
    //@formatter:on
    ;

    private final Integer value;
    private final String message;

    Status(Integer value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public Integer getValue() { return value; }
}
