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
     * 目标ID。
     */
    private String targetId;

    public List<String> getUserIds() { return userIds; }
    public void setUserIds(List<String> userIds) { this.userIds = userIds; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    @Override
    public String toString() {
        return "RelationSaveDTO{userIds=" + userIds + ", targetId='" + targetId + "'}";
    }
}
