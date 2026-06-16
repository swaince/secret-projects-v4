package com.dfec.soft.secret.common.constants;

/**
 * 用户关系类型。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "关系类型", code = "relation_type")
public enum RelationType implements DictionaryElement<String> {
    //@formatter:off
    DEPT("DEPT", "部门"),
    POST("POST", "岗位"),
    ROLE("ROLE", "角色"),
    //@formatter:on
    ;

    private final String value;
    private final String message;

    RelationType(String value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public String getValue() { return value; }
}
