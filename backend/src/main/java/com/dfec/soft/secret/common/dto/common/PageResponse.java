package com.dfec.soft.secret.common.dto.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 分页响应。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class PageResponse<T> {
    /**
     * 记录列表。
     */
    private List<T> records;
    /**
     * 总记录数。
     */
    private Long total;
    /**
     * 当前页码。
     */
    private Long page;
    /**
     * 每页条数。
     */
    private Long size;

    public static <T> PageResponse<T> of(IPage<T> page) {
        PageResponse<T> r = new PageResponse<>();
        r.records = page.getRecords(); r.total = page.getTotal();
        r.page = page.getCurrent(); r.size = page.getSize(); return r;
    }

    public static <T> PageResponse<T> of(List<T> records, IPage<?> page) {
        PageResponse<T> r = new PageResponse<>();
        r.records = records; r.total = page.getTotal();
        r.page = page.getCurrent(); r.size = page.getSize(); return r;
    }
    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Long getPage() { return page; }
    public void setPage(Long page) { this.page = page; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
}
