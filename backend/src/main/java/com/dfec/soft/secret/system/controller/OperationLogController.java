package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.audit.OperationLogDTO;
import com.dfec.soft.secret.system.dto.request.OperationLogPageRequest;
import com.dfec.soft.secret.system.service.OperationLogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志查询接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/logs/operation")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 分页查询操作日志。
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    @GetMapping
    public R<PageResponse<OperationLogDTO>> page(@Valid OperationLogPageRequest request) {
        return R.ok(operationLogService.page(request));
    }

    /**
     * 根据 ID 获取操作日志详情。
     *
     * @param logId 日志 ID
     * @return 操作日志详情
     */
    @GetMapping("/{logId}")
    public R<OperationLogDTO> getById(@PathVariable String logId) {
        return R.ok(operationLogService.getById(logId));
    }
}
