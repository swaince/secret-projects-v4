package com.dfec.soft.secret.common.dto.common;

/**
 * 统一响应封装。
 *
 * @param <T> 数据类型
 * @author zhangth
 * @since 1.0.0
 */
@SuppressWarnings("PMD")
public class R<T> {

    /**
     * 状态码。
     */
    private int code;

    /**
     * 消息。
     */
    private String message;

    /**
     * 数据。
     */
    private T data;

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>(); r.code = 200; r.message = "success"; r.data = data; return r;
    }
    public static <T> R<T> ok() { return ok(null); }
    public static <T> R<T> err(int code, String message) {
        R<T> r = new R<>(); r.code = code; r.message = message; return r;
    }
    public static <T> R<T> err(String message) { return err(500, message); }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
