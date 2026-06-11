package com.dfec.soft.secret.system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.system.mapper.DictMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 字典控制器测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DictControllerTest {

    private final MockMvc mockMvc;
    private final DictMapper dictMapper;
    private final ObjectMapper objectMapper;

    DictControllerTest(MockMvc mockMvc, DictMapper dictMapper, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.dictMapper = dictMapper;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        dictMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
    }

    @Test
    void shouldCreateAndGetDict() throws Exception {
        DictDTO request = new DictDTO();
        request.setDictName("测试字典");
        request.setDictCode("test_dict");

        String responseJson = mockMvc.perform(post("/dicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.dictName").value("测试字典"))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String dictId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("dictId").toString();

        mockMvc.perform(get("/dicts/" + dictId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.dictName").value("测试字典"));
    }

    @Test
    void shouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/dicts")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void shouldDeleteDict() throws Exception {
        DictDTO request = new DictDTO();
        request.setDictName("待删除");
        request.setDictCode("to_delete");

        String responseJson = mockMvc.perform(post("/dicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String dictId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("dictId").toString();

        mockMvc.perform(delete("/dicts/" + dictId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value(dictId));
    }
}
