package com.dfec.soft.secret.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 用户关系实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_user_relation")
public class SysUserRelation {
    /**
     * 关系ID。
     */
    @TableId(type = IdType.INPUT)
    private String relationId;

    /**
     * 用户ID。
     */
    @TableField("user_id")
    private String userId;

    /**
     * 关系类型 DEPT=部门 POST=岗位 ROLE=角色。
     */
    @TableField("relation_type")
    private String relationType;

    /**
     * 目标ID。
     */
    @TableField("target_id")
    private String targetId;

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

    public String getRelationId() { return relationId; }
    public void setRelationId(String relationId) { this.relationId = relationId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
