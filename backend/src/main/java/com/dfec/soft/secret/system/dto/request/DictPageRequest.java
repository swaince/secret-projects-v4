package com.dfec.soft.secret.system.dto.request;

import com.dfec.soft.secret.common.dto.request.QueryRequest;

/**
 * 字典分页查询请求。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class DictPageRequest extends QueryRequest {
    /**
     * 字典名称。
     */
    private String dictName;

    public String getDictName() { return dictName; }
    public void setDictName(String dictName) { this.dictName = dictName; }
}
