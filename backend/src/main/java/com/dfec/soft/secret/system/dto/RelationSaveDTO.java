package com.dfec.soft.secret.system.dto;

import java.util.List;

/**
 * 用户关系保存请求 DTO。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class RelationSaveDTO {
    /**
     * 用户ID列表。
     */
    private List<String> userIds;

    /**
     * 目标ID列表。
     */
    private List<String> targetIds;

    public List<String> getUserIds() { return userIds; }
    public void setUserIds(List<String> userIds) { this.userIds = userIds; }
    public List<String> getTargetIds() { return targetIds; }
    public void setTargetIds(List<String> targetIds) { this.targetIds = targetIds; }

    @Override
    public String toString() {
        return "RelationSaveDTO{userIds=" + userIds + ", targetIds=" + targetIds + "}";
    }
}
