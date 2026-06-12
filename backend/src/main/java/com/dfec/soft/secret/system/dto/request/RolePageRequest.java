package com.dfec.soft.secret.system.dto.request;

import com.dfec.soft.secret.common.dto.request.QueryRequest;

/**
 * 角色分页查询请求。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class RolePageRequest extends QueryRequest {
    /**
     * 角色名称。
     */
    private String roleName;
    /**
     * 角色编码。
     */
    private String roleCode;

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
}
