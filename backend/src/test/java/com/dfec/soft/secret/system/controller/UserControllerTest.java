package com.dfec.soft.secret.system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.UserDTO;
import com.dfec.soft.secret.system.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 用户管理接口测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserControllerTest {

    private final MockMvc mockMvc;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    UserControllerTest(MockMvc mockMvc, UserMapper userMapper, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        userMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserDTO user = new UserDTO();
        user.setUsername("testuser");
        user.setPassword("Test@123456");
        user.setGender("男");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.userId").exists())
            .andExpect(jsonPath("$.data.username").value("testuser"))
            .andExpect(jsonPath("$.data.gender").value("男"))
            .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void shouldQueryUsers() throws Exception {
        UserDTO user = new UserDTO();
        user.setUsername("queryuser");
        user.setPassword("Test@123456");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/users")
                .param("username", "query"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records.length()").value(1))
            .andExpect(jsonPath("$.data.records[0].username").value("queryuser"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserDTO user = new UserDTO();
        user.setUsername("updateuser");
        user.setPassword("Test@123456");

        String responseJson = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        @SuppressWarnings("unchecked")
        String userId = ((Map<String, Object>) objectMapper.convertValue(response.getData(), Map.class))
                .get("userId").toString();

        UserDTO updateRequest = new UserDTO();
        updateRequest.setGender("女");

        mockMvc.perform(put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.gender").value("女"))
            .andExpect(jsonPath("$.data.username").value("updateuser"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserDTO user = new UserDTO();
        user.setUsername("deleteuser");
        user.setPassword("Test@123456");

        String responseJson = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        @SuppressWarnings("unchecked")
        String userId = ((Map<String, Object>) objectMapper.convertValue(response.getData(), Map.class))
                .get("userId").toString();

        mockMvc.perform(delete("/users/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(userId));

        mockMvc.perform(get("/users")
                .param("username", "deleteuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.records.length()").value(0));
    }

    @Test
    void shouldReturnConflictWhenDuplicateUsername() throws Exception {
        UserDTO user = new UserDTO();
        user.setUsername("dupuser");
        user.setPassword("Test@123456");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(409));
    }
}
