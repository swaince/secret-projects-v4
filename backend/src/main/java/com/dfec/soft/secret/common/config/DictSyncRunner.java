package com.dfec.soft.secret.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.constants.Builtin;
import com.dfec.soft.secret.common.constants.DataValueType;
import com.dfec.soft.secret.common.constants.Deleted;
import com.dfec.soft.secret.common.constants.Dictionary;
import com.dfec.soft.secret.common.constants.DictionaryElement;
import com.dfec.soft.secret.common.constants.Status;
import com.dfec.soft.secret.common.service.UidService;
import com.dfec.soft.secret.system.entity.SysDict;
import com.dfec.soft.secret.system.entity.SysDictItem;
import com.dfec.soft.secret.system.mapper.DictItemMapper;
import com.dfec.soft.secret.system.mapper.DictMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用启动时自动扫描 @Dictionary 枚举并全量同步内置字典到数据库。
 *
 * <p>设计意图：内置字典以代码枚举为唯一真相来源，每次启动时全量刷新，
 * 确保数据库与代码保持一致。采用 Delete-then-Insert 策略，整体在事务内执行。</p>
 *
 * @author zhangth
 * @since 1.0.0
 */
@Component
public class DictSyncRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictSyncRunner.class);
    private static final String BASE_PACKAGE = "com.dfec.soft.secret";

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final UidService uidService;

    /**
     * 构造器注入依赖。
     *
     * @param dictMapper     字典 Mapper
     * @param dictItemMapper 字典项 Mapper
     * @param uidService     ID 生成服务
     */
    public DictSyncRunner(DictMapper dictMapper, DictItemMapper dictItemMapper, UidService uidService) {
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
        this.uidService = uidService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            syncBuiltinDicts();
        } catch (Exception e) {
            LOGGER.error("内置字典同步失败", e);
            throw e;
        }
    }

    /**
     * 全量同步内置字典：扫描→删除→插入。
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncBuiltinDicts() {
        LOGGER.info("开始同步内置字典...");
        List<Class<? extends DictionaryElement<?>>> enumClasses = scanDictionaryEnums();
        LOGGER.info("扫描发现 {} 个 @Dictionary 枚举", enumClasses.size());

        deleteBuiltinDicts();
        insertBuiltinDicts(enumClasses);

        LOGGER.info("内置字典同步完成");
    }

    /**
     * 扫描基础包下所有标注 @Dictionary 且实现 DictionaryElement 的枚举类。
     *
     * @return 枚举类列表
     */
    @SuppressWarnings("unchecked")
    private List<Class<? extends DictionaryElement<?>>> scanDictionaryEnums() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Dictionary.class));

        List<Class<? extends DictionaryElement<?>>> result = new ArrayList<>();
        Set<BeanDefinition> candidates = scanner.findCandidateComponents(BASE_PACKAGE);

        for (BeanDefinition beanDefinition : candidates) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                if (clazz.isEnum() && DictionaryElement.class.isAssignableFrom(clazz)) {
                    result.add((Class<? extends DictionaryElement<?>>) clazz);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("无法加载类: {}", beanDefinition.getBeanClassName(), e);
            }
        }
        return result;
    }

    /**
     * 删除所有内置字典及字典项。先删字典项，再删字典。
     */
    private void deleteBuiltinDicts() {
        dictItemMapper.delete(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getBuiltIn, Builtin.YES.getValue()));
        dictMapper.delete(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getBuiltIn, Builtin.YES.getValue()));
    }

    /**
     * 将扫描到的枚举类全量插入到字典表和字典项表。
     *
     * @param enumClasses 枚举类列表
     */
    private void insertBuiltinDicts(List<Class<? extends DictionaryElement<?>>> enumClasses) {
        for (Class<? extends DictionaryElement<?>> enumClass : enumClasses) {
            Dictionary annotation = enumClass.getAnnotation(Dictionary.class);
            DictionaryElement<?>[] constants = enumClass.getEnumConstants();
            if (constants == null || constants.length == 0) {
                continue;
            }

            String dictId = uidService.next();
            String dataValueType = inferDataValueType(constants[0].getValue());

            SysDict dict = new SysDict();
            dict.setDictId(dictId);
            dict.setDictName(annotation.name());
            dict.setDictCode(annotation.code());
            dict.setDataValueType(dataValueType);
            dict.setBuiltIn(Builtin.YES.getValue());
            dict.setStatus(Status.ENABLED.getValue());
            dict.setDeleted(Deleted.NO.getValue());
            dictMapper.insert(dict);

            for (int i = 0; i < constants.length; i++) {
                DictionaryElement<?> element = constants[i];
                SysDictItem item = new SysDictItem();
                item.setDictItemId(uidService.next());
                item.setDictId(dictId);
                item.setItemKey(element.getCode());
                item.setItemLabel(element.getMessage());
                item.setItemValue(String.valueOf(element.getValue()));
                item.setSortOrder(i);
                item.setBuiltIn(Builtin.YES.getValue());
                item.setStatus(Status.ENABLED.getValue());
                item.setDeleted(Deleted.NO.getValue());
                dictItemMapper.insert(item);
            }
        }
    }

    /**
     * 根据枚举值的运行时类型推断数据类型编码。
     *
     * @param value 枚举元素的值
     * @return 数据类型编码
     */
    private String inferDataValueType(Object value) {
        if (value instanceof Integer || value instanceof Long) {
            return DataValueType.NUMBER.getValue();
        } else if (value instanceof Boolean) {
            return DataValueType.BOOLEAN.getValue();
        }
        return DataValueType.STRING.getValue();
    }
}
