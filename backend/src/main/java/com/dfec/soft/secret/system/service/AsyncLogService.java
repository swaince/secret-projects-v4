package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.system.entity.SysAccessLog;
import com.dfec.soft.secret.system.entity.SysOperationLog;
import com.dfec.soft.secret.system.mapper.AccessLogMapper;
import com.dfec.soft.secret.system.mapper.OperationLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步日志写入服务，将日志持久化操作从请求线程中剥离。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
public class AsyncLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncLogService.class);

    private final AccessLogMapper accessLogMapper;
    private final OperationLogMapper operationLogMapper;

    public AsyncLogService(AccessLogMapper accessLogMapper, OperationLogMapper operationLogMapper) {
        this.accessLogMapper = accessLogMapper;
        this.operationLogMapper = operationLogMapper;
    }

    /**
     * 异步保存访问日志。
     *
     * @param log 访问日志实体
     */
    @Async("logTaskExecutor")
    public void saveAccessLog(SysAccessLog log) {
        try {
            accessLogMapper.insert(log);
        } catch (Exception e) {
            LOGGER.error("保存访问日志失败", e);
        }
    }

    /**
     * 异步保存操作日志。
     *
     * @param log 操作日志实体
     */
    @Async("logTaskExecutor")
    public void saveOperationLog(SysOperationLog log) {
        try {
            operationLogMapper.insert(log);
        } catch (Exception e) {
            LOGGER.error("保存操作日志失败", e);
        }
    }
}
