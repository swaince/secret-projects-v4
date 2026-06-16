package com.dfec.soft.secret.system.service;

import java.util.List;

/**
 * 用户关系服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface UserRelationService {

    /**
     * 批量保存用户关系（先删后增）。
     *
     * @param relationType 关系类型
     * @param userIds      用户 ID 列表
     * @param targetId     目标 ID
     * @param createdBy    创建人
     */
    void saveBatch(String relationType, List<String> userIds, String targetId, String createdBy);

    /**
     * 查询用户关联的目标 ID 列表。
     *
     * @param relationType 关系类型
     * @param userId       用户 ID
     * @return 目标 ID 列表
     */
    List<String> getTargetIds(String relationType, String userId);
}
