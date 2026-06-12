package com.dfec.soft.secret.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 岗位实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_post")
public class SysPost {
    /**
     * 岗位ID。
     */
    @TableId(type = IdType.INPUT)
    private String postId;

    /**
     * 岗位名称。
     */
    @TableField("post_name")
    private String postName;

    /**
     * 岗位编码。
     */
    @TableField("post_code")
    private String postCode;

    /**
     * 岗位级别。
     */
    @TableField("post_level")
    private Integer postLevel;

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

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getPostName() { return postName; }
    public void setPostName(String postName) { this.postName = postName; }
    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }
    public Integer getPostLevel() { return postLevel; }
    public void setPostLevel(Integer postLevel) { this.postLevel = postLevel; }
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
