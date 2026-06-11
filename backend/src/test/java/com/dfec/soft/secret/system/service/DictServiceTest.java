package com.dfec.soft.secret.system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.system.dto.request.DictPageRequest;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.system.entity.SysDict;
import com.dfec.soft.secret.system.mapper.DictMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 字典服务测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class DictServiceTest {

    private final DictService dictService;
    private final DictMapper dictMapper;

    DictServiceTest(DictService dictService, DictMapper dictMapper) {
        this.dictService = dictService;
        this.dictMapper = dictMapper;
    }

    @BeforeEach
    void setUp() {
        dictMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    void shouldPageDictionaries() {
        createDict("testName1", "testCode1");
        createDict("testName2", "testCode2");

        DictPageRequest request = new DictPageRequest();
        request.setPage(1L);
        request.setSize(10L);

        PageResponse<DictDTO> response = dictService.page(request);

        assertThat(response.getRecords()).hasSize(2);
        assertThat(response.getTotal()).isEqualTo(2L);
        assertThat(response.getPage()).isEqualTo(1L);
        assertThat(response.getSize()).isEqualTo(10L);
    }

    @Test
    void shouldCreateDictionary() {
        DictDTO request = new DictDTO();
        request.setDictName("性别");
        request.setDictCode("sex");
        request.setDataValueType("string");
        request.setRemark("性别字典");

        DictDTO result = dictService.create(request, "test-user");

        assertThat(result.getDictId()).isNotNull();
        assertThat(result.getDictName()).isEqualTo("性别");
        assertThat(result.getDictCode()).isEqualTo("sex");
        assertThat(dictMapper.selectById(result.getDictId())).isNotNull();
    }

    @Test
    void shouldDeleteDictionary() {
        String dictId = createDict("testName", "testCode");

        String deletedId = dictService.deleteById(dictId, "test-user");

        assertThat(deletedId).isEqualTo(dictId);
        assertThat(dictMapper.selectById(dictId)).isNull();
    }

    @Test
    void shouldBatchDelete() {
        String id1 = createDict("name1", "code1");
        String id2 = createDict("name2", "code2");
        List<String> ids = Arrays.asList(id1, id2);

        List<String> result = dictService.delete(ids, "test-user");

        assertThat(result).containsExactly(id1, id2);
        assertThat(dictMapper.selectById(id1)).isNull();
        assertThat(dictMapper.selectById(id2)).isNull();
    }

    @Test
    void shouldRejectDuplicateDictCode() {
        createDict("字典A", "dup_code");

        DictDTO request = new DictDTO();
        request.setDictName("字典B");
        request.setDictCode("dup_code");

        assertThatThrownBy(() -> dictService.create(request, "test-user"))
            .isInstanceOf(OuterException.class)
            .hasMessageContaining("字典编码已存在");
    }

    private String createDict(String dictName, String dictCode) {
        DictDTO request = new DictDTO();
        request.setDictName(dictName);
        request.setDictCode(dictCode);
        return dictService.create(request, "test-user").getDictId();
    }
}
