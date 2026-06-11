package com.dfec.soft.secret.common.constants;

/**
 * 是否删除。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "是否删除", code = "deleted")
public enum Deleted implements DictionaryElement<Integer> {
    //@formatter:off
    NO(0, "未删除"),
    YES(1, "已删除"),
    //@formatter:on
    ;

    private final Integer value;
    private final String message;

    Deleted(Integer value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public Integer getValue() { return value; }
}
