package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.system.dto.audit.AccessLogDTO;
import com.dfec.soft.secret.system.dto.request.AccessLogPageRequest;

/**
 * 访问日志服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface AccessLogService {

    /**
     * 分页查询访问日志。
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    PageResponse<AccessLogDTO> page(AccessLogPageRequest request);

    /**
     * 根据 ID 获取访问日志详情。
     *
     * @param logId 日志 ID
     * @return 访问日志 DTO
     */
    AccessLogDTO getById(String logId);
}
