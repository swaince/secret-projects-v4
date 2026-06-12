package com.dfec.soft.secret.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 角色实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_role")
public class SysRole {
    /**
     * 角色ID。
     */
    @TableId(type = IdType.INPUT)
    private String roleId;

    /**
     * 角色名称。
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色编码。
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 排序。
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态 1-启用 0-禁用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否内置 1-是 0-否。
     */
    @TableField("built_in")
    private Integer builtIn;

    /**
     * 是否删除 1-是 0-否。
     */
    @TableField("deleted")
    private Integer deleted;

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

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getBuiltIn() { return builtIn; }
    public void setBuiltIn(Integer builtIn) { this.builtIn = builtIn; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
