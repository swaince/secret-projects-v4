package com.dfec.soft.secret.system.dto.audit;

import java.time.LocalDateTime;

/**
 * 操作日志DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class OperationLogDTO {
    /**
     * 日志ID。
     */
    private String logId;

    /**
     * 请求ID。
     */
    private String requestId;

    /**
     * 请求URL。
     */
    private String requestUrl;

    /**
     * 请求方法。
     */
    private String requestMethod;

    /**
     * 请求头。
     */
    private String requestHeaders;

    /**
     * 请求参数。
     */
    private String requestParams;

    /**
     * 请求体。
     */
    private String requestBody;

    /**
     * 响应状态码。
     */
    private Integer responseStatus;

    /**
     * 耗时(毫秒)。
     */
    private Long durationMs;

    /**
     * 客户端IP。
     */
    private String clientIp;

    /**
     * User-Agent。
     */
    private String userAgent;

    /**
     * 用户ID。
     */
    private String userId;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 异常堆栈。
     */
    private String exceptionStack;

    /**
     * 控制器类。
     */
    private String controllerClass;

    /**
     * 控制器方法。
     */
    private String controllerMethod;

    /**
     * 创建时间。
     */
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

    @Override
    public String toString() {
        return "OperationLogDTO{logId='" + logId + "', requestUrl='" + requestUrl + "', requestMethod='" + requestMethod + "'}";
    }
}
