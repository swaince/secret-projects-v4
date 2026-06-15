package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.system.dto.audit.OperationLogDTO;
import com.dfec.soft.secret.system.dto.request.OperationLogPageRequest;
import com.dfec.soft.secret.system.entity.SysOperationLog;
import com.dfec.soft.secret.system.mapper.OperationLogMapper;
import com.dfec.soft.secret.system.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationLogServiceImpl.class);

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public PageResponse<OperationLogDTO> page(OperationLogPageRequest request) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(request.getRequestUrl())) {
            wrapper.like(SysOperationLog::getRequestUrl, request.getRequestUrl());
        }
        if (StringUtils.isNotBlank(request.getUsername())) {
            wrapper.like(SysOperationLog::getUsername, request.getUsername());
        }
        if (request.getResponseStatus() != null) {
            wrapper.eq(SysOperationLog::getResponseStatus, request.getResponseStatus());
        }
        if (StringUtils.isNotBlank(request.getRequestMethod())) {
            wrapper.eq(SysOperationLog::getRequestMethod, request.getRequestMethod());
        }
        if (StringUtils.isNotBlank(request.getControllerMethod())) {
            wrapper.like(SysOperationLog::getControllerMethod, request.getControllerMethod());
        }
        if (request.getStartTime() != null) {
            wrapper.ge(SysOperationLog::getCreatedAt, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.le(SysOperationLog::getCreatedAt, request.getEndTime());
        }
        long total = operationLogMapper.selectCount(wrapper);
        wrapper.orderByDesc(SysOperationLog::getCreatedAt);
        Page<SysOperationLog> pageParam = new Page<>(request.getPage(), request.getSize(), false);
        Page<SysOperationLog> pageResult = operationLogMapper.selectPage(pageParam, wrapper);
        pageResult.setTotal(total);
        LOGGER.info("操作日志分页查询，page={}, size={}, total={}", request.getPage(), request.getSize(), total);
        return PageResponse.of(pageResult.getRecords().stream().map(this::toDTO).toList(), pageResult);
    }

    @Override
    public OperationLogDTO getById(String logId) {
        SysOperationLog entity = operationLogMapper.selectById(logId);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "操作日志不存在");
        }
        return toDTO(entity);
    }

    private OperationLogDTO toDTO(SysOperationLog entity) {
        OperationLogDTO dto = new OperationLogDTO();
        dto.setLogId(entity.getLogId());
        dto.setRequestId(entity.getRequestId());
        dto.setRequestUrl(entity.getRequestUrl());
        dto.setRequestMethod(entity.getRequestMethod());
        dto.setRequestHeaders(entity.getRequestHeaders());
        dto.setRequestParams(entity.getRequestParams());
        dto.setRequestBody(entity.getRequestBody());
        dto.setResponseStatus(entity.getResponseStatus());
        dto.setDurationMs(entity.getDurationMs());
        dto.setClientIp(entity.getClientIp());
        dto.setUserAgent(entity.getUserAgent());
        dto.setUserId(entity.getUserId());
        dto.setUsername(entity.getUsername());
        dto.setExceptionStack(entity.getExceptionStack());
        dto.setControllerClass(entity.getControllerClass());
        dto.setControllerMethod(entity.getControllerMethod());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
