package com.dfec.soft.secret.common.constants;

/**
 * 岗位级别。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "岗位级别", code = "post_level")
public enum PostLevel implements DictionaryElement<Integer> {
    //@formatter:off
    OPERATOR(1, "操作员"),
    AUDITOR(2, "审核员"),
    //@formatter:on
    ;

    private final Integer value;
    private final String message;

    PostLevel(Integer value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public Integer getValue() { return value; }
}
