package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.constants.Builtin;
import com.dfec.soft.secret.common.constants.Deleted;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.common.constants.Status;
import com.dfec.soft.secret.system.dto.common.DictItemDTO;
import com.dfec.soft.secret.system.dto.common.DictWithItemsDTO;
import com.dfec.soft.secret.system.entity.SysDict;
import com.dfec.soft.secret.system.entity.SysDictItem;
import com.dfec.soft.secret.system.mapstruct.DictItemStructMapper;
import com.dfec.soft.secret.system.mapper.DictItemMapper;
import com.dfec.soft.secret.system.mapper.DictMapper;
import com.dfec.soft.secret.system.service.DictItemService;
import com.dfec.soft.secret.common.service.UidService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 字典项服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
@Validated
public class DictItemServiceImpl implements DictItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictItemServiceImpl.class);

    private final DictItemMapper dictItemMapper;
    private final DictMapper dictMapper;
    private final DictItemStructMapper dictItemStructMapper;
    private final UidService uidService;

    public DictItemServiceImpl(DictItemMapper dictItemMapper, DictMapper dictMapper,
            DictItemStructMapper dictItemStructMapper, UidService uidService) {
        this.dictItemMapper = dictItemMapper;
        this.dictMapper = dictMapper;
        this.dictItemStructMapper = dictItemStructMapper;
        this.uidService = uidService;
    }

    /**
     * 根据字典 ID 查询字典项列表。
     *
     * @param dictId 字典 ID
     * @return 字典项 DTO 列表
     */
    @Override
    public List<DictItemDTO> listByDictId(String dictId) {
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictId, dictId)
                .eq(SysDictItem::getDeleted, Deleted.NO.getValue())
                .eq(SysDictItem::getStatus, Status.ENABLED.getValue())
                .orderByAsc(SysDictItem::getSortOrder);
        List<SysDictItem> list = dictItemMapper.selectList(wrapper);
        return dictItemStructMapper.entityToDTO(list);
    }

    /**
     * 创建字典项。
     *
     * @param dictId    所属字典 ID
     * @param request   字典项创建请求
     * @param createdBy 创建人
     * @return 创建后的字典项 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictItemDTO create(String dictId, DictItemDTO request, String createdBy) {
        SysDictItem entity = dictItemStructMapper.requestToEntity(request);
        entity.setDictItemId(uidService.next());
        entity.setDictId(dictId);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(createdBy);
        dictItemMapper.insert(entity);
        LOGGER.info("创建字典项，dictItemId={}, dictId={}, itemKey={}", entity.getDictItemId(), dictId, entity.getItemKey());
        return dictItemStructMapper.entityToDTO(entity);
    }

    /**
     * 更新字典项。
     *
     * @param itemId    字典项 ID
     * @param request   字典项更新请求
     * @param updatedBy 更新人
     * @return 更新后的字典项 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictItemDTO update(String itemId, DictItemDTO request, String updatedBy) {
        SysDictItem entity = selectByIdNotDeleted(itemId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置字典项不可修改");
        }
        dictItemStructMapper.updateEntity(entity, request);
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
        dictItemMapper.updateById(entity);
        LOGGER.info("更新字典项，itemId={}", itemId);
        return dictItemStructMapper.entityToDTO(entity);
    }

    /**
     * 批量删除字典项。
     *
     * @param itemIds 字典项 ID 列表
     * @param userId  操作用户 ID
     * @return 被删除的字典项 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> delete(List<String> itemIds, String userId) {
        for (String itemId : itemIds) {
            SysDictItem entity = selectByIdNotDeleted(itemId);
            if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
                throw new OuterException(BizCode.FORBIDDEN, "内置字典项不可删除");
            }
            entity.setUpdatedBy(userId);
            entity.setUpdatedAt(LocalDateTime.now());
            dictItemMapper.updateById(entity);
        }
        dictItemMapper.deleteByIds(itemIds);
        return itemIds;
    }

    /**
     * 根据 ID 删除字典项。
     *
     * @param itemId 字典项 ID
     * @param userId 操作用户 ID
     * @return 被删除的字典项 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(String itemId, String userId) {
        SysDictItem entity = selectByIdNotDeleted(itemId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置字典项不可删除");
        }
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(LocalDateTime.now());
        dictItemMapper.updateById(entity);
        dictItemMapper.deleteById(itemId);
        return itemId;
    }

    /**
     * 根据字典 ID 删除所有字典项。
     *
     * @param dictId 字典 ID
     * @return 被删除的字典项 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> deleteByDictId(String dictId) {
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictId, dictId)
                .eq(SysDictItem::getDeleted, Deleted.NO.getValue());
        List<SysDictItem> items = dictItemMapper.selectList(wrapper);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> ids = items.stream().map(SysDictItem::getDictItemId).collect(Collectors.toList());
        dictItemMapper.deleteByIds(ids);
        LOGGER.info("按字典 ID 删除字典项，dictId={}, 删除数量={}", dictId, ids.size());
        return ids;
    }

    /**
     * 根据字典编码删除所有字典项。
     *
     * @param dictCode 字典编码
     * @return 被删除的字典项 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> deleteByDictCode(String dictCode) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDictCode, dictCode)
                .eq(SysDict::getDeleted, Deleted.NO.getValue());
        SysDict dict = dictMapper.selectOne(wrapper);
        if (dict == null) {
            LOGGER.info("按字典编码删除字典项，dictCode={} 不存在", dictCode);
            return Collections.emptyList();
        }
        return deleteByDictId(dict.getDictId());
    }

    @Override
    public DictWithItemsDTO getWithItemsByCode(String dictCode) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDictCode, dictCode)
                .eq(SysDict::getDeleted, Deleted.NO.getValue())
                .eq(SysDict::getStatus, Status.ENABLED.getValue());
        SysDict dict = dictMapper.selectOne(wrapper);
        if (dict == null) {
            return null;
        }
        DictWithItemsDTO dto = new DictWithItemsDTO();
        dto.setDictId(dict.getDictId());
        dto.setDictName(dict.getDictName());
        dto.setDictCode(dict.getDictCode());
        dto.setDataValueType(dict.getDataValueType());
        dto.setItems(listByDictId(dict.getDictId()));
        return dto;
    }

    private SysDictItem selectByIdNotDeleted(String itemId) {
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictItemId, itemId)
                .eq(SysDictItem::getDeleted, Deleted.NO.getValue());
        SysDictItem entity = dictItemMapper.selectOne(wrapper);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "字典项不存在");
        }
        return entity;
    }

}
