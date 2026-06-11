package com.dfec.soft.secret.system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.system.dto.common.DictItemDTO;
import com.dfec.soft.secret.system.mapper.DictItemMapper;
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
    private final DictItemMapper dictItemMapper;
    private final ObjectMapper objectMapper;

    DictControllerTest(MockMvc mockMvc, DictMapper dictMapper, DictItemMapper dictItemMapper,
                       ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        dictItemMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
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

    @Test
    void shouldRejectDuplicateDictCode() throws Exception {
        DictDTO first = new DictDTO();
        first.setDictName("字典A");
        first.setDictCode("dup_code");
        mockMvc.perform(post("/dicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)))
            .andExpect(status().isOk());

        DictDTO second = new DictDTO();
        second.setDictName("字典B");
        second.setDictCode("dup_code");
        mockMvc.perform(post("/dicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(second)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void shouldDeleteItemsByDictCode() throws Exception {
        DictDTO dict = new DictDTO();
        dict.setDictName("测试字典");
        dict.setDictCode("by_code_test");
        String responseJson = mockMvc.perform(post("/dicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dict)))
            .andReturn().getResponse().getContentAsString();
        R<?> response = objectMapper.readValue(responseJson, R.class);
        String dictId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("dictId").toString();

        DictItemDTO item = new DictItemDTO();
        item.setItemKey("k1");
        item.setItemValue("v1");
        mockMvc.perform(post("/dicts/" + dictId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/dicts/items/by-code")
                .param("dictCode", "by_code_test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());

        mockMvc.perform(get("/dicts/" + dictId + "/items"))
            .andExpect(jsonPath("$.data.length()").value(0));
    }
}
