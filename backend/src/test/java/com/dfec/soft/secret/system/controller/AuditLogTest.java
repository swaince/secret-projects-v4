package com.dfec.soft.secret.system.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dfec.soft.secret.system.mapper.AccessLogMapper;
import com.dfec.soft.secret.system.mapper.OperationLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 审计日志集成测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AuditLogTest {

    private final MockMvc mockMvc;
    private final AccessLogMapper accessLogMapper;
    private final OperationLogMapper operationLogMapper;

    AuditLogTest(MockMvc mockMvc, AccessLogMapper accessLogMapper, OperationLogMapper operationLogMapper) {
        this.mockMvc = mockMvc;
        this.accessLogMapper = accessLogMapper;
        this.operationLogMapper = operationLogMapper;
    }

    @BeforeEach
    void setUp() {
        accessLogMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        operationLogMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
    }

    @Test
    void shouldRecordAccessLogOnRequest() throws Exception {
        mockMvc.perform(get("/dicts"))
                .andExpect(status().isOk());

        Thread.sleep(500);

        long count = accessLogMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertEquals(1, count);
    }

    @Test
    void shouldRecordOperationLogOnControllerRequest() throws Exception {
        mockMvc.perform(get("/dicts"))
                .andExpect(status().isOk());

        Thread.sleep(500);

        long count = operationLogMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertEquals(1, count);
    }

    @Test
    void shouldRecordCorrectRequestUrlInAccessLog() throws Exception {
        mockMvc.perform(get("/dicts"))
                .andExpect(status().isOk());

        Thread.sleep(500);

        var log = accessLogMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertNotNull(log);
        assertEquals("/dicts", log.getRequestUrl());
        assertEquals("GET", log.getRequestMethod());
        assertNotNull(log.getRequestId());
    }

    @Test
    void shouldRecordCorrectRequestUrlInOperationLog() throws Exception {
        mockMvc.perform(get("/dicts"))
                .andExpect(status().isOk());

        Thread.sleep(500);

        var log = operationLogMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertNotNull(log);
        assertEquals("/dicts", log.getRequestUrl());
        assertEquals("GET", log.getRequestMethod());
        assertNotNull(log.getControllerClass());
        assertNotNull(log.getControllerMethod());
    }
}
