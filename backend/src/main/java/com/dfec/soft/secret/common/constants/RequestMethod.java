package com.dfec.soft.secret.common.constants;

/**
 * HTTP 请求方式。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Dictionary(name = "请求方式", code = "request_method")
public enum RequestMethod implements DictionaryElement<String> {
    //@formatter:off
    GET("GET", "GET"),
    POST("POST", "POST"),
    PUT("PUT", "PUT"),
    DELETE("DELETE", "DELETE"),
    PATCH("PATCH", "PATCH"),
    HEAD("HEAD", "HEAD"),
    OPTIONS("OPTIONS", "OPTIONS"),
    //@formatter:on
    ;

    private final String value;
    private final String message;

    RequestMethod(String value, String message) { this.value = value; this.message = message; }

    @Override public String getCode() { return name(); }
    @Override public String getMessage() { return message; }
    @Override public String getValue() { return value; }
}
