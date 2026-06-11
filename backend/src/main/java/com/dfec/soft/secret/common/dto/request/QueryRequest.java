package com.dfec.soft.secret.common.dto.request;

/**
 * 通用查询请求。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class QueryRequest {
    /**
     * 页码。
     */
    private Long page = 1L;
    /**
     * 每页条数。
     */
    private Long size = 10L;
    /**
     * 排序字段。
     */
    private String sortField;
    /**
     * 排序方式。
     */
    private String sortOrder;

    public Long getPage() { return page; }
    public void setPage(Long page) { this.page = page; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}
