package com.dfec.soft.secret.system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.PostDTO;
import com.dfec.soft.secret.system.entity.SysPost;
import com.dfec.soft.secret.system.mapper.PostMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 岗位控制器测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PostControllerTest {

    private final MockMvc mockMvc;
    private final PostMapper postMapper;
    private final ObjectMapper objectMapper;

    PostControllerTest(MockMvc mockMvc, PostMapper postMapper, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.postMapper = postMapper;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        postMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldCreateAndGetPost() throws Exception {
        PostDTO request = new PostDTO();
        request.setPostName("测试岗位");
        request.setPostCode("test_post");
        request.setPostLevel(1);

        String responseJson = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.postName").value("测试岗位"))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String postId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("postId").toString();

        mockMvc.perform(get("/posts/" + postId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.postName").value("测试岗位"));
    }

    @Test
    void shouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/posts")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void shouldDeletePost() throws Exception {
        PostDTO request = new PostDTO();
        request.setPostName("待删除");
        request.setPostCode("to_delete");

        String responseJson = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String postId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("postId").toString();

        mockMvc.perform(delete("/posts/" + postId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(postId));
    }

    @Test
    void shouldRejectDuplicatePostCode() throws Exception {
        PostDTO first = new PostDTO();
        first.setPostName("岗位A");
        first.setPostCode("dup_code");
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)))
            .andExpect(status().isOk());

        PostDTO second = new PostDTO();
        second.setPostName("岗位B");
        second.setPostCode("dup_code");
        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(second)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void shouldBatchDeleteSkipBuiltin() throws Exception {
        PostDTO normal = new PostDTO();
        normal.setPostName("普通岗位");
        normal.setPostCode("normal_batch");
        String normalJson = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(normal)))
            .andReturn().getResponse().getContentAsString();
        R<?> normalResp = objectMapper.readValue(normalJson, R.class);
        String normalId = objectMapper.convertValue(normalResp.getData(),
            java.util.Map.class).get("postId").toString();

        PostDTO builtin = new PostDTO();
        builtin.setPostName("内置岗位");
        builtin.setPostCode("builtin_batch");
        String builtinJson = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(builtin)))
            .andReturn().getResponse().getContentAsString();
        R<?> builtinResp = objectMapper.readValue(builtinJson, R.class);
        String builtinId = objectMapper.convertValue(builtinResp.getData(),
            java.util.Map.class).get("postId").toString();

        SysPost entity = postMapper.selectById(builtinId);
        entity.setBuiltIn(1);
        postMapper.updateById(entity);

        mockMvc.perform(delete("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(normalId, builtinId))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0]").value(normalId));
    }

    @Test
    void shouldUpdatePost() throws Exception {
        PostDTO create = new PostDTO();
        create.setPostName("原名称");
        create.setPostCode("upd_code");
        String createJson = mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
            .andReturn().getResponse().getContentAsString();
        R<?> createResp = objectMapper.readValue(createJson, R.class);
        String postId = objectMapper.convertValue(createResp.getData(),
            java.util.Map.class).get("postId").toString();

        PostDTO update = new PostDTO();
        update.setPostName("新名称");
        update.setPostCode("upd_code");
        update.setPostLevel(5);

        mockMvc.perform(put("/posts/" + postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.postName").value("新名称"))
            .andExpect(jsonPath("$.data.postLevel").value(5));
    }
}
