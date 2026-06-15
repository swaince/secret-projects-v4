package com.dfec.soft.secret.system.dto.request;

import com.dfec.soft.secret.common.dto.request.QueryRequest;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 访问日志分页请求。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class AccessLogPageRequest extends QueryRequest {
    /**
     * 请求URL。
     */
    private String requestUrl;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 响应状态码。
     */
    private Integer responseStatus;

    /**
     * 请求方式。
     */
    private String requestMethod;

    /**
     * 开始时间。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getResponseStatus() { return responseStatus; }
    public void setResponseStatus(Integer responseStatus) { this.responseStatus = responseStatus; }
    public String getRequestMethod() { return requestMethod; }
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
