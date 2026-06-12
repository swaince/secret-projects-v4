package com.dfec.soft.secret.system.dto.request;

import com.dfec.soft.secret.common.dto.request.QueryRequest;

/**
 * 岗位分页查询请求。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class PostPageRequest extends QueryRequest {
    /**
     * 岗位名称。
     */
    private String postName;
    /**
     * 岗位编码。
     */
    private String postCode;
    /**
     * 岗位级别。
     */
    private Integer postLevel;

    public String getPostName() { return postName; }
    public void setPostName(String postName) { this.postName = postName; }
    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }
    public Integer getPostLevel() { return postLevel; }
    public void setPostLevel(Integer postLevel) { this.postLevel = postLevel; }
}
