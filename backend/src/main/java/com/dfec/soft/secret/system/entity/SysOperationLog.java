package com.dfec.soft.secret.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 操作日志实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_operation_log")
public class SysOperationLog {
    /**
     * 日志ID。
     */
    @TableId(type = IdType.INPUT)
    private String logId;

    /**
     * 请求ID。
     */
    @TableField("request_id")
    private String requestId;

    /**
     * 请求URL。
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * 请求方法。
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求头。
     */
    @TableField("request_headers")
    private String requestHeaders;

    /**
     * 请求参数。
     */
    @TableField("request_params")
    private String requestParams;

    /**
     * 请求体。
     */
    @TableField("request_body")
    private String requestBody;

    /**
     * 响应状态码。
     */
    @TableField("response_status")
    private Integer responseStatus;

    /**
     * 耗时(毫秒)。
     */
    @TableField("duration_ms")
    private Long durationMs;

    /**
     * 客户端IP。
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * User-Agent。
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 用户ID。
     */
    @TableField("user_id")
    private String userId;

    /**
     * 用户名。
     */
    @TableField("username")
    private String username;

    /**
     * 异常堆栈。
     */
    @TableField("exception_stack")
    private String exceptionStack;

    /**
     * 控制器类。
     */
    @TableField("controller_class")
    private String controllerClass;

    /**
     * 控制器方法。
     */
    @TableField("controller_method")
    private String controllerMethod;

    /**
     * 创建时间。
     */
    @TableField("create_at")
    private LocalDateTime createdAt;

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }
    public String getRequestMethod() { return requestMethod; }
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }
    public String getRequestHeaders() { return requestHeaders; }
    public void setRequestHeaders(String requestHeaders) { this.requestHeaders = requestHeaders; }
    public String getRequestParams() { return requestParams; }
    public void setRequestParams(String requestParams) { this.requestParams = requestParams; }
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    public Integer getResponseStatus() { return responseStatus; }
    public void setResponseStatus(Integer responseStatus) { this.responseStatus = responseStatus; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getExceptionStack() { return exceptionStack; }
    public void setExceptionStack(String exceptionStack) { this.exceptionStack = exceptionStack; }
    public String getControllerClass() { return controllerClass; }
    public void setControllerClass(String controllerClass) { this.controllerClass = controllerClass; }
    public String getControllerMethod() { return controllerMethod; }
    public void setControllerMethod(String controllerMethod) { this.controllerMethod = controllerMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
