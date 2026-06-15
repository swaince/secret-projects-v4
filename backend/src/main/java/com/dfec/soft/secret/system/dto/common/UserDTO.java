package com.dfec.soft.secret.system.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.dfec.soft.secret.common.validation.group.Create;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 用户 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class UserDTO {

    /**
     * 用户 ID。
     */
    private String userId;
    /**
     * 用户名。
     */
    @NotBlank(message = "用户名不能为空", groups = {Create.class})
    private String username;
    /**
     * 显示名。
     */
    private String displayName;
    /**
     * 密码。
     */
    @NotBlank(message = "密码不能为空", groups = {Create.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
    /**
     * 性别。
     */
    private String gender;
    /**
     * 状态 1-启用 0-禁用。
     */
    private Integer status;
    /**
     * 账号过期时间。
     */
    private LocalDateTime accountExpireTime;
    /**
     * 密码过期时间。
     */
    private LocalDateTime passwordExpireTime;
    /**
     * 最近登录时间。
     */
    private LocalDateTime lastLoginTime;
    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getAccountExpireTime() { return accountExpireTime; }
    public void setAccountExpireTime(LocalDateTime accountExpireTime) { this.accountExpireTime = accountExpireTime; }
    public LocalDateTime getPasswordExpireTime() { return passwordExpireTime; }
    public void setPasswordExpireTime(LocalDateTime passwordExpireTime) { this.passwordExpireTime = passwordExpireTime; }
    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "UserDTO{userId='" + userId + "', username='" + username + "'}";
    }
}
