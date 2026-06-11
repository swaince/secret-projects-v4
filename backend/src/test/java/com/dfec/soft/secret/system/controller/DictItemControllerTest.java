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
 * 字典项控制器测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DictItemControllerTest {

    private final MockMvc mockMvc;
    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final ObjectMapper objectMapper;

    DictItemControllerTest(MockMvc mockMvc, DictMapper dictMapper,
                           DictItemMapper dictItemMapper, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
        this.objectMapper = objectMapper;
    }

    private String dictId;

    @BeforeEach
    void setUp() throws Exception {
        dictItemMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());
        dictMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>());

        DictDTO request = new DictDTO();
        request.setDictName("测试字典");
        request.setDictCode("test_dict");

        String responseJson = mockMvc.perform(post("/dicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        dictId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("dictId").toString();
    }

    @Test
    void shouldCreateAndListItem() throws Exception {
        DictItemDTO request = new DictItemDTO();
        request.setItemKey("ENABLED");
        request.setItemValue("1");
        request.setSortOrder(0);

        mockMvc.perform(post("/dicts/" + dictId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.itemKey").value("ENABLED"));

        mockMvc.perform(get("/dicts/" + dictId + "/items"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/dicts/" + dictId + "/items"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldDeleteAllItemsByDictId() throws Exception {
        DictItemDTO item = new DictItemDTO();
        item.setItemKey("k1");
        item.setItemValue("v1");
        mockMvc.perform(post("/dicts/" + dictId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/dicts/" + dictId + "/items/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());

        mockMvc.perform(get("/dicts/" + dictId + "/items"))
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        DictItemDTO request = new DictItemDTO();
        request.setItemKey("key");
        request.setItemValue("val");

        String responseJson = mockMvc.perform(post("/dicts/" + dictId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String itemId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("dictItemId").toString();

        mockMvc.perform(delete("/dicts/" + dictId + "/items/" + itemId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
