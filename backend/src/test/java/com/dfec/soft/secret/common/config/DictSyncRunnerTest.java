package com.dfec.soft.secret.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.system.entity.SysDict;
import com.dfec.soft.secret.system.entity.SysDictItem;
import com.dfec.soft.secret.system.mapper.DictItemMapper;
import com.dfec.soft.secret.system.mapper.DictMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * DictSyncRunner 测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DictSyncRunnerTest {

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final DictSyncRunner dictSyncRunner;

    DictSyncRunnerTest(DictMapper dictMapper, DictItemMapper dictItemMapper, DictSyncRunner dictSyncRunner) {
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
        this.dictSyncRunner = dictSyncRunner;
    }

    @Test
    void shouldSyncBuiltinDictsOnStartup() {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getBuiltIn, 1);
        Long count = dictMapper.selectCount(wrapper);
        assertThat(count).isGreaterThanOrEqualTo(5L);
    }

    @Test
    void shouldMapDictFieldsCorrectly() {
        LambdaQueryWrapper<SysDict> dictWrapper = new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictCode, "status");
        SysDict statusDict = dictMapper.selectOne(dictWrapper);
        assertThat(statusDict).isNotNull();
        assertThat(statusDict.getDictName()).isEqualTo("状态");
        assertThat(statusDict.getDataValueType()).isEqualTo("NUMBER");
        assertThat(statusDict.getBuiltIn()).isEqualTo(1);
        assertThat(statusDict.getStatus()).isEqualTo(1);
        assertThat(statusDict.getDeleted()).isEqualTo(0);
    }

    @Test
    void shouldMapDictItemFieldsCorrectly() {
        LambdaQueryWrapper<SysDict> dictWrapper = new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictCode, "status");
        SysDict statusDict = dictMapper.selectOne(dictWrapper);
        assertThat(statusDict).isNotNull();

        LambdaQueryWrapper<SysDictItem> itemWrapper = new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, statusDict.getDictId())
                .eq(SysDictItem::getItemKey, "ENABLED");
        SysDictItem enabledItem = dictItemMapper.selectOne(itemWrapper);
        assertThat(enabledItem).isNotNull();
        assertThat(enabledItem.getItemLabel()).isEqualTo("启用");
        assertThat(enabledItem.getItemValue()).isEqualTo("1");
        assertThat(enabledItem.getSortOrder()).isEqualTo(0);
        assertThat(enabledItem.getBuiltIn()).isEqualTo(1);
    }

    @Test
    void shouldBeIdempotent() {
        LambdaQueryWrapper<SysDict> beforeWrapper = new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getBuiltIn, 1);
        Long countBefore = dictMapper.selectCount(beforeWrapper);

        dictSyncRunner.syncBuiltinDicts();

        Long countAfter = dictMapper.selectCount(beforeWrapper);
        assertThat(countAfter).isEqualTo(countBefore);
    }

    @Test
    void shouldNotAffectBusinessDicts() {
        SysDict bizDict = new SysDict();
        bizDict.setDictId("biz-test-001");
        bizDict.setDictName("业务字典");
        bizDict.setDictCode("biz_test_sync");
        bizDict.setBuiltIn(0);
        bizDict.setStatus(1);
        bizDict.setDeleted(0);
        dictMapper.insert(bizDict);

        dictSyncRunner.syncBuiltinDicts();

        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictCode, "biz_test_sync");
        SysDict found = dictMapper.selectOne(wrapper);
        assertThat(found).isNotNull();
        assertThat(found.getDictName()).isEqualTo("业务字典");
    }
}
