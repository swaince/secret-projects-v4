package com.dfec.soft.secret.system.dto.common;

import java.util.List;

/**
 * 字典及其字典项 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class DictWithItemsDTO {

    /**
     * 字典 ID。
     */
    private String dictId;
    /**
     * 字典名称。
     */
    private String dictName;
    /**
     * 字典编码。
     */
    private String dictCode;
    /**
     * 数据值类型。
     */
    private String dataValueType;
    /**
     * 字典项列表。
     */
    private List<DictItemDTO> items;

    public String getDictId() { return dictId; }
    public void setDictId(String dictId) { this.dictId = dictId; }
    public String getDictName() { return dictName; }
    public void setDictName(String dictName) { this.dictName = dictName; }
    public String getDictCode() { return dictCode; }
    public void setDictCode(String dictCode) { this.dictCode = dictCode; }
    public String getDataValueType() { return dataValueType; }
    public void setDataValueType(String dataValueType) { this.dataValueType = dataValueType; }
    public List<DictItemDTO> getItems() { return items; }
    public void setItems(List<DictItemDTO> items) { this.items = items; }

    @Override
    public String toString() {
        return "DictWithItemsDTO{dictId='" + dictId + "', dictCode='" + dictCode + "', items=" + (items == null ? 0 : items.size()) + "}";
    }
}
