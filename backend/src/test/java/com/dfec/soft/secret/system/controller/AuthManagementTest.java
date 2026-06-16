package com.dfec.soft.secret.system.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dfec.soft.secret.system.mapper.AuthorizationMapper;
import com.dfec.soft.secret.system.mapper.UserRelationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 权限管理集成测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AuthManagementTest {

    private final MockMvc mockMvc;
    private final UserRelationMapper userRelationMapper;
    private final AuthorizationMapper authorizationMapper;

    AuthManagementTest(MockMvc mockMvc, UserRelationMapper userRelationMapper,
            AuthorizationMapper authorizationMapper) {
        this.mockMvc = mockMvc;
        this.userRelationMapper = userRelationMapper;
        this.authorizationMapper = authorizationMapper;
    }

    @BeforeEach
    void setUp() {
        userRelationMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        authorizationMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
    }

    @Test
    void shouldSaveAndQueryUserRelations() throws Exception {
        String json = "{\"userIds\":[\"u1\",\"u2\"],\"targetId\":\"role-001\"}";
        mockMvc.perform(post("/relations/ROLE")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        String response = mockMvc.perform(get("/relations/ROLE/u1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertTrue(response.contains("role-001"));

        long count = userRelationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertEquals(2, count);
    }

    @Test
    void shouldSaveAndQueryAuthorizations() throws Exception {
        String json = "{\"subjectIds\":[\"s1\",\"s2\"],\"menuIds\":[\"m1\",\"m2\"]}";
        mockMvc.perform(post("/authorizations/USER")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        String response = mockMvc.perform(get("/authorizations/USER/s1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertTrue(response.contains("m1"));
        assertTrue(response.contains("m2"));

        long count = authorizationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertEquals(4, count);
    }

    @Test
    void shouldReplaceRelationsOnBatchSave() throws Exception {
        String json1 = "{\"userIds\":[\"u3\"],\"targetId\":\"dept-001\"}";
        mockMvc.perform(post("/relations/DEPT")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json1))
                .andExpect(status().isOk());

        long count1 = userRelationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertEquals(1, count1);

        String json2 = "{\"userIds\":[\"u3\"],\"targetId\":\"dept-002\"}";
        mockMvc.perform(post("/relations/DEPT")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isOk());

        long count2 = userRelationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertEquals(1, count2);

        String response = mockMvc.perform(get("/relations/DEPT/u3"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertTrue(response.contains("dept-002"));
    }

    @Test
    void shouldReplaceAuthorizationsOnBatchSave() throws Exception {
        String json1 = "{\"subjectIds\":[\"s3\"],\"menuIds\":[\"m3\"]}";
        mockMvc.perform(post("/authorizations/ROLE")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json1))
                .andExpect(status().isOk());

        long count1 = authorizationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertEquals(1, count1);

        String json2 = "{\"subjectIds\":[\"s3\"],\"menuIds\":[\"m4\",\"m5\"]}";
        mockMvc.perform(post("/authorizations/ROLE")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json2))
                .andExpect(status().isOk());

        long count2 = authorizationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        assertEquals(2, count2);

        String response = mockMvc.perform(get("/authorizations/ROLE/s3"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertTrue(response.contains("m4"));
        assertTrue(response.contains("m5"));
    }
}
