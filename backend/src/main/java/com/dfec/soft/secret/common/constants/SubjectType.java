package com.dfec.soft.secret.common.constants;

/**
 * 授权主体类型。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "授权主体", code = "subject_type")
public enum SubjectType implements DictionaryElement<String> {
    //@formatter:off
    USER("USER", "用户"),
    DEPT("DEPT", "部门"),
    POST("POST", "岗位"),
    ROLE("ROLE", "角色"),
    //@formatter:on
    ;

    private final String value;
    private final String message;

    SubjectType(String value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public String getValue() { return value; }
}
