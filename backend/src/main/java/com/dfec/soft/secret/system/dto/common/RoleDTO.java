package com.dfec.soft.secret.system.dto.common;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 角色 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class RoleDTO {

    /**
     * 角色 ID。
     */
    private String roleId;
    /**
     * 角色名称。
     */
    @NotBlank(message = "角色名称不能为空", groups = {Create.class, Update.class})
    private String roleName;
    /**
     * 角色编码。
     */
    @NotBlank(message = "角色编码不能为空", groups = {Create.class, Update.class})
    private String roleCode;
    /**
     * 排序。
     */
    private Integer sortOrder;
    /**
     * 备注。
     */
    private String remark;
    /**
     * 状态 1-启用 0-禁用。
     */
    private Integer status;
    /**
     * 是否内置 1-是 0-否。
     */
    private Integer builtIn;
    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "RoleDTO{roleId='" + roleId + "', roleName='" + roleName + "', roleCode='" + roleCode + "'}";
    }
}
