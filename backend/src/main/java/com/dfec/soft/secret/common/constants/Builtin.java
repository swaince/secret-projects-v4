package com.dfec.soft.secret.common.constants;

/**
 * 是否内置。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "是否内置", code = "built_in")
public enum Builtin implements DictionaryElement<Integer> {
    //@formatter:off
    YES(1, "内置"),
    NO(0, "非内置"),
    //@formatter:on
    ;

    private final Integer value;
    private final String message;

    Builtin(Integer value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public Integer getValue() { return value; }
}
