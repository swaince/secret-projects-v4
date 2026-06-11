package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.constants.Builtin;
import com.dfec.soft.secret.common.constants.Deleted;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.system.dto.request.DictPageRequest;
import com.dfec.soft.secret.system.entity.SysDict;
import com.dfec.soft.secret.system.entity.SysDictItem;
import com.dfec.soft.secret.system.mapstruct.DictStructMapper;
import com.dfec.soft.secret.system.mapper.DictItemMapper;
import com.dfec.soft.secret.system.mapper.DictMapper;
import com.dfec.soft.secret.system.service.DictService;
import com.dfec.soft.secret.common.service.UidService;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 字典服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
@Validated
public class DictServiceImpl implements DictService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictServiceImpl.class);

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final DictStructMapper dictStructMapper;
    private final UidService uidService;

    public DictServiceImpl(DictMapper dictMapper, DictItemMapper dictItemMapper, DictStructMapper dictStructMapper,
            UidService uidService) {
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
        this.dictStructMapper = dictStructMapper;
        this.uidService = uidService;
    }

    /**
     * 分页查询字典列表。
     *
     * @param request 分页请求参数
     * @return 字典分页响应
     */
    @Override
    public PageResponse<DictDTO> page(DictPageRequest request) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDeleted, Deleted.NO.getValue());
        if (StringUtils.isNotBlank(request.getDictName())) {
            wrapper.like(SysDict::getDictName, request.getDictName());
        }
        if (StringUtils.isNotBlank(request.getDataValueType())) {
            wrapper.eq(SysDict::getDataValueType, request.getDataValueType());
        }
        long total = dictMapper.selectCount(wrapper);
        wrapper.orderByAsc(SysDict::getDictCode);
        Page<SysDict> pageParam = new Page<>(request.getPage(), request.getSize(), false);
        Page<SysDict> pageResult = dictMapper.selectPage(pageParam, wrapper);
        pageResult.setTotal(total);
        LOGGER.info("page={}, size={}, total={}", request.getPage(), request.getSize(), total);
        return PageResponse.of(dictStructMapper.entityToDTO(pageResult.getRecords()), pageResult);
    }

    /**
     * 根据 ID 查询字典。
     *
     * @param dictId 字典 ID
     * @return 字典 DTO
     */
    @Override
    public DictDTO getById(String dictId) {
        SysDict entity = selectByIdNotDeleted(dictId);
        return dictStructMapper.entityToDTO(entity);
    }

    /**
     * 创建字典。
     *
     * @param request   字典创建请求
     * @param createdBy 创建人
     * @return 创建后的字典 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDTO create(DictDTO request, String createdBy) {
        LambdaQueryWrapper<SysDict> codeCheck = new LambdaQueryWrapper<>();
        codeCheck.eq(SysDict::getDictCode, request.getDictCode())
                .eq(SysDict::getDeleted, Deleted.NO.getValue());
        if (dictMapper.selectCount(codeCheck) > 0) {
            throw new OuterException(BizCode.CONFLICT, "字典编码已存在");
        }
        SysDict entity = dictStructMapper.requestToEntity(request);
        entity.setDictId(uidService.next());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(createdBy);
        dictMapper.insert(entity);
        LOGGER.info("创建字典，dictId={}, dictCode={}", entity.getDictId(), entity.getDictCode());
        return dictStructMapper.entityToDTO(entity);
    }

    /**
     * 更新字典。
     *
     * @param dictId    字典 ID
     * @param request   字典更新请求
     * @param updatedBy 更新人
     * @return 更新后的字典 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictDTO update(String dictId, DictDTO request, String updatedBy) {
        SysDict entity = selectByIdNotDeleted(dictId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置字典不可修改");
        }
        dictStructMapper.updateEntity(entity, request);
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
        dictMapper.updateById(entity);
        LOGGER.info("更新字典，dictId={}", dictId);
        return dictStructMapper.entityToDTO(entity);
    }

    /**
     * 批量删除字典。
     *
     * @param dictIds 字典 ID 列表
     * @param userId  操作用户 ID
     * @return 被删除的字典 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> delete(List<String> dictIds, String userId) {
        for (String dictId : dictIds) {
            deleteById(dictId, userId);
        }
        return dictIds;
    }

    /**
     * 根据 ID 删除字典及其字典项。
     *
     * @param dictId 字典 ID
     * @param userId 操作用户 ID
     * @return 被删除的字典 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(String dictId, String userId) {
        SysDict entity = selectByIdNotDeleted(dictId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置字典不可删除");
        }
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(LocalDateTime.now());
        dictMapper.updateById(entity);
        dictMapper.deleteById(dictId);
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictId, dictId);
        dictItemMapper.delete(wrapper);
        LOGGER.info("删除字典及字典项，dictId={}", dictId);
        return dictId;
    }

    private SysDict selectByIdNotDeleted(String dictId) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDictId, dictId)
                .eq(SysDict::getDeleted, Deleted.NO.getValue());
        SysDict entity = dictMapper.selectOne(wrapper);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "字典不存在");
        }
        return entity;
    }

}
