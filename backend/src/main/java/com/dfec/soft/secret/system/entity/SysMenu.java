package com.dfec.soft.secret.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 系统菜单实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_menu")
public class SysMenu {
    /**
     * 菜单ID。
     */
    @TableId(type = IdType.INPUT)
    private String menuId;

    /**
     * 父菜单ID。
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 菜单名称。
     */
    @TableField("menu_name")
    private String menuName;

    /**
     * 菜单类型 D=目录 M=菜单 B=按钮。
     */
    @TableField("menu_type")
    private String menuType;

    /**
     * 路由路径。
     */
    @TableField("path")
    private String path;

    /**
     * 组件路径。
     */
    @TableField("component")
    private String component;

    /**
     * 图标。
     */
    @TableField("icon")
    private String icon;

    /**
     * 排序。
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态 1=启用 0=禁用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 权限标识。
     */
    @TableField("permission")
    private String permission;

    /**
     * 是否可见 1=可见 0=隐藏。
     */
    @TableField("visible")
    private Integer visible;

    /**
     * 重定向路径。
     */
    @TableField("redirect")
    private String redirect;

    /**
     * 是否需要认证 1=需要 0=不需要。
     */
    @TableField("require_auth")
    private Integer requireAuth;

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
     * 是否删除 1=是 0=否。
     */
    @TableField("deleted")
    private Integer deleted;

    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuType() { return menuType; }
    public void setMenuType(String menuType) { this.menuType = menuType; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    public Integer getVisible() { return visible; }
    public void setVisible(Integer visible) { this.visible = visible; }
    public String getRedirect() { return redirect; }
    public void setRedirect(String redirect) { this.redirect = redirect; }
    public Integer getRequireAuth() { return requireAuth; }
    public void setRequireAuth(Integer requireAuth) { this.requireAuth = requireAuth; }
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
