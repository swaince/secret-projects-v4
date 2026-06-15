package com.dfec.soft.secret.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 系统用户实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_user")
public class SysUser {
    /**
     * 用户ID。
     */
    @TableId(type = IdType.INPUT)
    private String userId;

    /**
     * 用户名。
     */
    @TableField("username")
    private String username;

    /**
     * 显示名。
     */
    @TableField("display_name")
    private String displayName;

    /**
     * 密码(BCrypt加密)。
     */
    @TableField("password")
    private String password;

    /**
     * 性别。
     */
    @TableField("gender")
    private String gender;

    /**
     * 状态 1-启用 0-禁用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 账号过期时间。
     */
    @TableField("account_expire_time")
    private LocalDateTime accountExpireTime;

    /**
     * 密码过期时间。
     */
    @TableField("password_expire_time")
    private LocalDateTime passwordExpireTime;

    /**
     * 最近登录时间。
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间。
     */
    @TableField(value = "create_at")
    private LocalDateTime createdAt;

    /**
     * 创建人。
     */
    @TableField(value = "create_by")
    private String createdBy;

    /**
     * 更新时间。
     */
    @TableField(value = "update_at")
    private LocalDateTime updatedAt;

    /**
     * 更新人。
     */
    @TableField(value = "update_by")
    private String updatedBy;

    /**
     * 是否删除 1-是 0-否。
     */
    @TableField("deleted")
    private Integer deleted;

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
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
