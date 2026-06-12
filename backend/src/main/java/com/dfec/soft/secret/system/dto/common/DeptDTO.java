package com.dfec.soft.secret.system.dto.common;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class DeptDTO {

    /**
     * 部门 ID。
     */
    private String deptId;
    /**
     * 部门名称。
     */
    @NotBlank(message = "部门名称不能为空", groups = {Create.class, Update.class})
    private String deptName;
    /**
     * 部门编码。
     */
    @NotBlank(message = "部门编码不能为空", groups = {Create.class, Update.class})
    private String deptCode;
    /**
     * 父部门 ID。
     */
    private String parentId;
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
    /**
     * 子部门列表（树结构响应用）。
     */
    private List<DeptDTO> children;

    public String getDeptId() { return deptId; }
    public void setDeptId(String deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
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
    public List<DeptDTO> getChildren() { return children; }
    public void setChildren(List<DeptDTO> children) { this.children = children; }

    @Override
    public String toString() {
        return "DeptDTO{deptId='" + deptId + "', deptName='" + deptName + "', deptCode='" + deptCode + "'}";
    }
}
