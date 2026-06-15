package com.dfec.soft.secret.system.dto.common;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class MenuDTO {

    /**
     * 菜单 ID。
     */
    private String menuId;

    /**
     * 父菜单 ID。
     */
    private String parentId;

    /**
     * 菜单名称。
     */
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    /**
     * 菜单类型 D=目录 M=菜单 B=按钮。
     */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /**
     * 路由路径。
     */
    private String path;

    /**
     * 组件路径。
     */
    private String component;

    /**
     * 图标。
     */
    private String icon;

    /**
     * 排序。
     */
    private Integer sortOrder;

    /**
     * 状态 1=启用 0=禁用。
     */
    private Integer status;

    /**
     * 权限标识。
     */
    private String permission;

    /**
     * 是否可见 1=可见 0=隐藏。
     */
    private Integer visible;

    /**
     * 重定向路径。
     */
    private String redirect;

    /**
     * 是否需要认证 1=需要 0=不需要。
     */
    private Integer requireAuth;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 子菜单列表（树形结构用）。
     */
    private List<MenuDTO> children;

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
    public List<MenuDTO> getChildren() { return children; }
    public void setChildren(List<MenuDTO> children) { this.children = children; }

    @Override
    public String toString() {
        return "MenuDTO{menuId='" + menuId + "', menuName='" + menuName + "'}";
    }
}
