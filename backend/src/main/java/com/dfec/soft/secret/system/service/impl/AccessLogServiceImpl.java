package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.system.dto.audit.AccessLogDTO;
import com.dfec.soft.secret.system.dto.request.AccessLogPageRequest;
import com.dfec.soft.secret.system.entity.SysAccessLog;
import com.dfec.soft.secret.system.mapper.AccessLogMapper;
import com.dfec.soft.secret.system.service.AccessLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 访问日志服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
public class AccessLogServiceImpl implements AccessLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogServiceImpl.class);

    private final AccessLogMapper accessLogMapper;

    public AccessLogServiceImpl(AccessLogMapper accessLogMapper) {
        this.accessLogMapper = accessLogMapper;
    }

    @Override
    public PageResponse<AccessLogDTO> page(AccessLogPageRequest request) {
        LambdaQueryWrapper<SysAccessLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(request.getRequestUrl())) {
            wrapper.like(SysAccessLog::getRequestUrl, request.getRequestUrl());
        }
        if (StringUtils.isNotBlank(request.getUsername())) {
            wrapper.like(SysAccessLog::getUsername, request.getUsername());
        }
        if (request.getResponseStatus() != null) {
            wrapper.eq(SysAccessLog::getResponseStatus, request.getResponseStatus());
        }
        if (StringUtils.isNotBlank(request.getRequestMethod())) {
            wrapper.eq(SysAccessLog::getRequestMethod, request.getRequestMethod());
        }
        if (request.getStartTime() != null) {
            wrapper.ge(SysAccessLog::getCreatedAt, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.le(SysAccessLog::getCreatedAt, request.getEndTime());
        }
        long total = accessLogMapper.selectCount(wrapper);
        wrapper.orderByDesc(SysAccessLog::getCreatedAt);
        Page<SysAccessLog> pageParam = new Page<>(request.getPage(), request.getSize(), false);
        Page<SysAccessLog> pageResult = accessLogMapper.selectPage(pageParam, wrapper);
        pageResult.setTotal(total);
        LOGGER.info("访问日志分页查询，page={}, size={}, total={}", request.getPage(), request.getSize(), total);
        return PageResponse.of(pageResult.getRecords().stream().map(this::toDTO).toList(), pageResult);
    }

    @Override
    public AccessLogDTO getById(String logId) {
        SysAccessLog entity = accessLogMapper.selectById(logId);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "访问日志不存在");
        }
        return toDTO(entity);
    }

    private AccessLogDTO toDTO(SysAccessLog entity) {
        AccessLogDTO dto = new AccessLogDTO();
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
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
