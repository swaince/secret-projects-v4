package com.dfec.soft.secret.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 菜单授权实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_authorization")
public class SysAuthorization {
    /**
     * 授权ID。
     */
    @TableId(type = IdType.INPUT)
    private String authId;

    /**
     * 主体类型 USER=用户 DEPT=部门 POST=岗位 ROLE=角色。
     */
    @TableField("subject_type")
    private String subjectType;

    /**
     * 主体ID。
     */
    @TableField("subject_id")
    private String subjectId;

    /**
     * 菜单ID。
     */
    @TableField("menu_id")
    private String menuId;

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

    public String getAuthId() { return authId; }
    public void setAuthId(String authId) { this.authId = authId; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
