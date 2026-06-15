package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.system.dto.audit.OperationLogDTO;
import com.dfec.soft.secret.system.dto.request.OperationLogPageRequest;

/**
 * 操作日志服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface OperationLogService {

    /**
     * 分页查询操作日志。
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    PageResponse<OperationLogDTO> page(OperationLogPageRequest request);

    /**
     * 根据 ID 获取操作日志详情。
     *
     * @param logId 日志 ID
     * @return 操作日志 DTO
     */
    OperationLogDTO getById(String logId);
}
