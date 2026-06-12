package com.dfec.soft.secret.system.dto.common;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 岗位 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class PostDTO {

    /**
     * 岗位 ID。
     */
    private String postId;
    /**
     * 岗位名称。
     */
    @NotBlank(message = "岗位名称不能为空", groups = {Create.class, Update.class})
    private String postName;
    /**
     * 岗位编码。
     */
    @NotBlank(message = "岗位编码不能为空", groups = {Create.class, Update.class})
    private String postCode;
    /**
     * 岗位级别。
     */
    private Integer postLevel;
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "PostDTO{postId='" + postId + "', postName='" + postName + "', postCode='" + postCode + "'}";
    }
}
