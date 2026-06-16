package com.dfec.soft.secret.system.service;

import java.util.List;

/**
 * 菜单授权服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface AuthorizationService {

    /**
     * 批量保存菜单授权（先删后增）。
     *
     * @param subjectType 主体类型
     * @param subjectIds  主体 ID 列表
     * @param menuIds     菜单 ID 列表
     * @param createdBy   创建人
     */
    void saveBatch(String subjectType, List<String> subjectIds, List<String> menuIds, String createdBy);

    /**
     * 查询主体已授权的菜单 ID 列表。
     *
     * @param subjectType 主体类型
     * @param subjectId   主体 ID
     * @return 菜单 ID 列表
     */
    List<String> getMenuIds(String subjectType, String subjectId);
}
