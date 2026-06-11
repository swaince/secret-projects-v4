package com.dfec.soft.secret.system.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.system.dto.common.DictItemDTO;
import com.dfec.soft.secret.system.entity.SysDictItem;
import com.dfec.soft.secret.system.mapper.DictItemMapper;
import com.dfec.soft.secret.system.mapper.DictMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 字典项服务测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DictItemServiceTest {

    private final DictItemService dictItemService;
    private final DictItemMapper dictItemMapper;
    private final DictService dictService;
    private final DictMapper dictMapper;

    DictItemServiceTest(DictItemService dictItemService, DictItemMapper dictItemMapper,
                        DictService dictService, DictMapper dictMapper) {
        this.dictItemService = dictItemService;
        this.dictItemMapper = dictItemMapper;
        this.dictService = dictService;
        this.dictMapper = dictMapper;
    }

    private String dictId;

    @BeforeEach
    void setUp() {
        dictItemMapper.delete(new LambdaQueryWrapper<>());
        dictMapper.delete(new LambdaQueryWrapper<>());

        DictDTO request = new DictDTO();
        request.setDictName("测试字典");
        request.setDictCode("test_dict");
        DictDTO dict = dictService.create(request, "test-user");
        dictId = dict.getDictId();
    }

    @Test
    void shouldListByDictId() {
        insertItem("key1", "value1", 0);
        insertItem("key2", "value2", 1);

        List<DictItemDTO> items = dictItemService.listByDictId(dictId);

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getItemKey()).isEqualTo("key1");
        assertThat(items.get(1).getItemKey()).isEqualTo("key2");
    }

    @Test
    void shouldCreateItem() {
        DictItemDTO request = new DictItemDTO();
        request.setItemKey("new_key");
        request.setItemValue("new_value");
        request.setSortOrder(0);

        DictItemDTO result = dictItemService.create(dictId, request, "test-user");

        assertThat(result.getItemKey()).isEqualTo("new_key");
        assertThat(result.getItemValue()).isEqualTo("new_value");
        assertThat(result.getDictId()).isEqualTo(dictId);
    }

    @Test
    void shouldUpdateItem() {
        String itemId = insertItem("old_key", "old_value", 0);

        DictItemDTO request = new DictItemDTO();
        request.setItemKey("updated_key");
        request.setItemValue("updated_value");
        request.setSortOrder(10);
        DictItemDTO result = dictItemService.update(itemId, request, "test-user");

        assertThat(result.getItemKey()).isEqualTo("updated_key");
        assertThat(result.getItemValue()).isEqualTo("updated_value");
        assertThat(result.getSortOrder()).isEqualTo(10);
    }

    @Test
    void shouldDeleteItem() {
        String itemId = insertItem("key", "value", 0);

        String deletedId = dictItemService.deleteById(itemId, "test-user");

        assertThat(deletedId).isEqualTo(itemId);
        assertThat(dictItemMapper.selectById(itemId)).isNull();
    }

    @Test
    void shouldBatchDeleteItems() {
        String id1 = insertItem("k1", "v1", 0);
        String id2 = insertItem("k2", "v2", 1);

        List<String> deleted = dictItemService.delete(List.of(id1, id2), "test-user");

        assertThat(deleted).containsExactly(id1, id2);
        assertThat(dictItemMapper.selectById(id1)).isNull();
        assertThat(dictItemMapper.selectById(id2)).isNull();
    }

    @Test
    void shouldDeleteByDictId() {
        insertItem("k1", "v1", 0);
        insertItem("k2", "v2", 1);

        List<String> deleted = dictItemService.deleteByDictId(dictId);

        assertThat(deleted).hasSize(2);
        assertThat(dictItemService.listByDictId(dictId)).isEmpty();
    }

    @Test
    void shouldDeleteByDictCode() {
        insertItem("k1", "v1", 0);

        List<String> deleted = dictItemService.deleteByDictCode("test_dict");

        assertThat(deleted).hasSize(1);
        assertThat(dictItemService.listByDictId(dictId)).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenDeleteByCodeNotFound() {
        List<String> deleted = dictItemService.deleteByDictCode("nonexistent");

        assertThat(deleted).isEmpty();
    }

    private String insertItem(String key, String value, int sortOrder) {
        SysDictItem item = new SysDictItem();
        item.setDictItemId(java.util.UUID.randomUUID().toString().replace("-", ""));
        item.setDictId(dictId);
        item.setItemKey(key);
        item.setItemValue(value);
        item.setSortOrder(sortOrder);
        dictItemMapper.insert(item);
        return item.getDictItemId();
    }
}
