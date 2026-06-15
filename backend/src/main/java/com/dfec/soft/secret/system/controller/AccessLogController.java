package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.audit.AccessLogDTO;
import com.dfec.soft.secret.system.dto.request.AccessLogPageRequest;
import com.dfec.soft.secret.system.service.AccessLogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 访问日志查询接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/logs/access")
public class AccessLogController {

    private final AccessLogService accessLogService;

    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    /**
     * 分页查询访问日志。
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    @GetMapping
    public R<PageResponse<AccessLogDTO>> page(@Valid AccessLogPageRequest request) {
        return R.ok(accessLogService.page(request));
    }

    /**
     * 根据 ID 获取访问日志详情。
     *
     * @param logId 日志 ID
     * @return 访问日志详情
     */
    @GetMapping("/{logId}")
    public R<AccessLogDTO> getById(@PathVariable String logId) {
        return R.ok(accessLogService.getById(logId));
    }
}
