package com.dfec.soft.secret.system.dto.common;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 字典 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class DictDTO {

    /**
     * 字典 ID。
     */
    private String dictId;
    /**
     * 字典名称。
     */
    @NotBlank(message = "字典名称不能为空", groups = {Create.class, Update.class})
    private String dictName;
    /**
     * 字典编码。
     */
    @NotBlank(message = "字典编码不能为空", groups = {Create.class, Update.class})
    private String dictCode;
    /**
     * 数据值类型。
     */
    private String dataValueType;
    /**
     * 备注。
     */
    private String remark;
    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    public String getDictId() { return dictId; }
    public void setDictId(String dictId) { this.dictId = dictId; }
    public String getDictName() { return dictName; }
    public void setDictName(String dictName) { this.dictName = dictName; }
    public String getDictCode() { return dictCode; }
    public void setDictCode(String dictCode) { this.dictCode = dictCode; }
    public String getDataValueType() { return dataValueType; }
    public void setDataValueType(String dataValueType) { this.dataValueType = dataValueType; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "DictDTO{dictId='" + dictId + "', dictName='" + dictName + "', dictCode='" + dictCode + "'}";
    }
}
