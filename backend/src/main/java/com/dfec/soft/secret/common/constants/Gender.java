package com.dfec.soft.secret.common.constants;

/**
 * 性别。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "性别", code = "gender")
public enum Gender implements DictionaryElement<String> {
    //@formatter:off
    MALE("M", "男"),
    FEMALE("F", "女"),
    //@formatter:on
    ;

    private final String value;
    private final String message;

    Gender(String value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public String getValue() { return value; }
}
