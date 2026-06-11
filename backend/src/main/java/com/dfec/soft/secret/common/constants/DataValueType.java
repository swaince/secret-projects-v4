package com.dfec.soft.secret.common.constants;

/**
 * 数据类型。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "数据类型", code = "data_type")
public enum DataValueType implements DictionaryElement<String> {
    //@formatter:off
    STRING("STRING", "字符串"),
    NUMBER("NUMBER", "数字"),
    BOOLEAN("BOOLEAN", "布尔"),
    OBJECT("OBJECT", "对象"),
    ARRAY("ARRAY", "数组"),
    //@formatter:on
    ;

    private final String value;
    private final String message;

    DataValueType(String value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public String getValue() { return value; }
}
