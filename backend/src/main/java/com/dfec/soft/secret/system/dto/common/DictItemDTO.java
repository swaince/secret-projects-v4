package com.dfec.soft.secret.system.dto.common;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 字典项 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class DictItemDTO {

    /**
     * 字典项 ID。
     */
    private String dictItemId;
    /**
     * 字典 ID。
     */
    private String dictId;
    /**
     * 字典项键。
     */
    @NotBlank(message = "字典项键不能为空", groups = {Create.class, Update.class})
    private String itemKey;
    /**
     * 字典项标签。
     */
    private String itemLabel;
    /**
     * 字典项值。
     */
    @NotBlank(message = "字典项值不能为空", groups = {Create.class, Update.class})
    private String itemValue;
    /**
     * 排序。
     */
    private Integer sortOrder;
    /**
     * 备注。
     */
    private String remark;
    /**
     * 状态。
     */
    private Integer status;
    /**
     * 是否内置。
     */
    private Integer builtIn;
    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    public String getDictItemId() { return dictItemId; }
    public void setDictItemId(String dictItemId) { this.dictItemId = dictItemId; }
    public String getDictId() { return dictId; }
    public void setDictId(String dictId) { this.dictId = dictId; }
    public String getItemKey() { return itemKey; }
    public void setItemKey(String itemKey) { this.itemKey = itemKey; }
    public String getItemLabel() { return itemLabel; }
    public void setItemLabel(String itemLabel) { this.itemLabel = itemLabel; }
    public String getItemValue() { return itemValue; }
    public void setItemValue(String itemValue) { this.itemValue = itemValue; }
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
        return "DictItemDTO{dictItemId='" + dictItemId + "', itemKey='" + itemKey + "', itemValue='" + itemValue + "'}";
    }
}
