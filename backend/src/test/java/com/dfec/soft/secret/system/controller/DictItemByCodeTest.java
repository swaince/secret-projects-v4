package com.dfec.soft.secret.system.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.system.dto.common.DictItemDTO;
import com.dfec.soft.secret.system.entity.SysDictItem;
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
 * 根据字典编码查询字典及字典项接口测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DictItemByCodeTest {

    private final MockMvc mockMvc;
    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final ObjectMapper objectMapper;

    DictItemByCodeTest(MockMvc mockMvc, DictMapper dictMapper,
                       DictItemMapper dictItemMapper, ObjectMapper objectMapper) {
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
    void shouldReturnDictWithItems() throws Exception {
        DictDTO dict = new DictDTO();
        dict.setDictName("测试字典");
        dict.setDictCode("test_by_code");

        String responseJson = mockMvc.perform(post("/dicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dict)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String dictId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("dictId").toString();

        DictItemDTO item = new DictItemDTO();
        item.setItemKey("ENABLED");
        item.setItemValue("1");
        item.setSortOrder(0);
        mockMvc.perform(post("/dicts/" + dictId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/dicts/items/by-code")
                .param("dictCode", "test_by_code"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.dictCode").value("test_by_code"))
            .andExpect(jsonPath("$.data.dictName").value("测试字典"))
            .andExpect(jsonPath("$.data.items.length()").value(1))
            .andExpect(jsonPath("$.data.items[0].itemKey").value("ENABLED"));
    }

    @Test
    void shouldReturnNullDataWhenDictCodeNotExist() throws Exception {
        mockMvc.perform(get("/dicts/items/by-code")
                .param("dictCode", "non_existent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturnEmptyItemsWhenAllDisabled() throws Exception {
        DictDTO dict = new DictDTO();
        dict.setDictName("禁用测试");
        dict.setDictCode("disabled_items_test");

        String responseJson = mockMvc.perform(post("/dicts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dict)))
            .andReturn().getResponse().getContentAsString();

        R<?> response = objectMapper.readValue(responseJson, R.class);
        String dictId = objectMapper.convertValue(response.getData(),
            java.util.Map.class).get("dictId").toString();

        SysDictItem disabledItem = new SysDictItem();
        disabledItem.setDictItemId("disabled-item-1");
        disabledItem.setDictId(dictId);
        disabledItem.setItemKey("OFF");
        disabledItem.setItemValue("0");
        disabledItem.setSortOrder(0);
        disabledItem.setStatus(0);
        disabledItem.setDeleted(0);
        dictItemMapper.insert(disabledItem);

        mockMvc.perform(get("/dicts/items/by-code")
                .param("dictCode", "disabled_items_test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.dictCode").value("disabled_items_test"))
            .andExpect(jsonPath("$.data.items.length()").value(0));
    }

    @Test
    void shouldRejectBlankDictCode() throws Exception {
        mockMvc.perform(get("/dicts/items/by-code")
                .param("dictCode", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(500));
    }
}
