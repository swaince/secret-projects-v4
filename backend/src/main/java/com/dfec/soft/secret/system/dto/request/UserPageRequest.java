package com.dfec.soft.secret.system.dto.request;

import com.dfec.soft.secret.common.dto.request.QueryRequest;

/**
 * 用户分页查询请求。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class UserPageRequest extends QueryRequest {
    /**
     * 用户名（模糊匹配）。
     */
    private String username;

    /**
     * 显示名（模糊匹配）。
     */
    private String displayName;

    /**
     * 性别（精确匹配）。
     */
    private String gender;

    /**
     * 状态（精确匹配）。
     */
    private Integer status;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
