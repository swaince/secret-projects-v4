package com.dfec.soft.secret.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 字典实体。
 *
 * @author zhangth
 * @since 1.0.0
 */
@TableName("sys_dict")
public class SysDict {
    /**
     * 字典ID。
     */
    @TableId(type = IdType.INPUT)
    private String dictId;

    /**
     * 字典名称。
     */
    @TableField("dict_name")
    private String dictName;

    /**
     * 字典编码。
     */
    @TableField("dict_code")
    private String dictCode;

    /**
     * 数据类型。
     */
    @TableField("data_value_type")
    private String dataValueType;

    /**
     * 备注。
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间。
     */
    @TableField(value = "create_at")
    private LocalDateTime createdAt;

    /**
     * 创建人。
     */
    @TableField(value = "create_by")
    private String createdBy;

    /**
     * 更新时间。
     */
    @TableField(value = "update_at")
    private LocalDateTime updatedAt;

    /**
     * 更新人。
     */
    @TableField(value = "update_by")
    private String updatedBy;

    /**
     * 状态 1-启用 0-禁用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否内置 1-是 0-否。
     */
    @TableField("built_in")
    private Integer builtIn;

    /**
     * 是否删除 1-是 0-否。
     */
    @TableField("deleted")
    private Integer deleted;

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
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getBuiltIn() { return builtIn; }
    public void setBuiltIn(Integer builtIn) { this.builtIn = builtIn; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
