package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.constants.Builtin;
import com.dfec.soft.secret.common.constants.Deleted;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.common.service.UidService;
import com.dfec.soft.secret.system.dto.common.DeptDTO;
import com.dfec.soft.secret.system.entity.SysDept;
import com.dfec.soft.secret.system.mapper.DeptMapper;
import com.dfec.soft.secret.system.mapstruct.DeptStructMapper;
import com.dfec.soft.secret.system.service.DeptService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 部门服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
@Validated
public class DeptServiceImpl implements DeptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeptServiceImpl.class);

    private final DeptMapper deptMapper;
    private final DeptStructMapper deptStructMapper;
    private final UidService uidService;

    public DeptServiceImpl(DeptMapper deptMapper, DeptStructMapper deptStructMapper, UidService uidService) {
        this.deptMapper = deptMapper;
        this.deptStructMapper = deptStructMapper;
        this.uidService = uidService;
    }

    /**
     * 获取部门树。
     *
     * @return 部门树列表
     */
    @Override
    public List<DeptDTO> tree() {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeleted, Deleted.NO.getValue());
        List<SysDept> allDepts = deptMapper.selectList(wrapper);
        List<DeptDTO> allDtos = deptStructMapper.entityToDTO(allDepts);

        Map<String, List<DeptDTO>> childrenMap = allDtos.stream()
            .filter(dto -> dto.getParentId() != null)
            .collect(Collectors.groupingBy(DeptDTO::getParentId));

        List<DeptDTO> roots = allDtos.stream()
            .filter(dto -> dto.getParentId() == null)
            .sorted(Comparator.comparingInt(d -> d.getSortOrder() == null ? 0 : d.getSortOrder()))
            .collect(Collectors.toList());

        for (DeptDTO root : roots) {
            buildChildren(root, childrenMap);
        }
        return roots;
    }

    /**
     * 根据 ID 查询部门。
     *
     * @param deptId 部门 ID
     * @return 部门 DTO
     */
    @Override
    public DeptDTO getById(String deptId) {
        SysDept entity = selectByIdNotDeleted(deptId);
        return deptStructMapper.entityToDTO(entity);
    }

    /**
     * 创建部门。
     *
     * @param request   部门创建请求
     * @param createdBy 创建人
     * @return 创建后的部门 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeptDTO create(DeptDTO request, String createdBy) {
        LambdaQueryWrapper<SysDept> codeCheck = new LambdaQueryWrapper<>();
        codeCheck.eq(SysDept::getDeptCode, request.getDeptCode())
                .eq(SysDept::getDeleted, Deleted.NO.getValue());
        if (deptMapper.selectCount(codeCheck) > 0) {
            throw new OuterException(BizCode.CONFLICT, "部门编码已存在");
        }
        if (request.getParentId() != null) {
            selectByIdNotDeleted(request.getParentId());
        }
        SysDept entity = deptStructMapper.requestToEntity(request);
        entity.setDeptId(uidService.next());
        if (entity.getSortOrder() == null || entity.getSortOrder() == 0) {
            LambdaQueryWrapper<SysDept> siblingWrapper = new LambdaQueryWrapper<>();
            if (request.getParentId() == null) {
                siblingWrapper.isNull(SysDept::getParentId);
            } else {
                siblingWrapper.eq(SysDept::getParentId, request.getParentId());
            }
            siblingWrapper.eq(SysDept::getDeleted, Deleted.NO.getValue());
            long count = deptMapper.selectCount(siblingWrapper);
            entity.setSortOrder((int) count + 1);
        }
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(createdBy);
        deptMapper.insert(entity);
        LOGGER.info("创建部门，deptId={}, deptCode={}", entity.getDeptId(), entity.getDeptCode());
        return deptStructMapper.entityToDTO(entity);
    }

    /**
     * 更新部门。
     *
     * @param deptId    部门 ID
     * @param request   部门更新请求
     * @param updatedBy 更新人
     * @return 更新后的部门 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeptDTO update(String deptId, DeptDTO request, String updatedBy) {
        SysDept entity = selectByIdNotDeleted(deptId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置部门不可修改");
        }
        deptStructMapper.updateEntity(entity, request);
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
        deptMapper.updateById(entity);
        LOGGER.info("更新部门，deptId={}", deptId);
        return deptStructMapper.entityToDTO(entity);
    }

    /**
     * 批量删除部门，跳过内置部门和有子部门的部门。
     *
     * @param ids    部门 ID 列表
     * @param userId 操作用户 ID
     * @return 被删除的部门 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> delete(List<String> ids, String userId) {
        List<String> deleted = new ArrayList<>();
        for (String deptId : ids) {
            SysDept entity = selectByIdNotDeleted(deptId);
            if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
                LOGGER.info("跳过内置部门，deptId={}", deptId);
                continue;
            }
            if (hasChildren(deptId)) {
                LOGGER.info("跳过有子部门的部门，deptId={}", deptId);
                continue;
            }
            entity.setUpdatedBy(userId);
            entity.setUpdatedAt(LocalDateTime.now());
            deptMapper.updateById(entity);
            deptMapper.deleteById(deptId);
            deleted.add(deptId);
        }
        return deleted;
    }

    /**
     * 根据 ID 删除部门。
     *
     * @param deptId 部门 ID
     * @param userId 操作用户 ID
     * @return 被删除的部门 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(String deptId, String userId) {
        SysDept entity = selectByIdNotDeleted(deptId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置部门不可删除");
        }
        if (hasChildren(deptId)) {
            throw new OuterException(BizCode.CONFLICT, "存在子部门，无法删除");
        }
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(LocalDateTime.now());
        deptMapper.updateById(entity);
        deptMapper.deleteById(deptId);
        LOGGER.info("删除部门，deptId={}", deptId);
        return deptId;
    }

    private void buildChildren(DeptDTO parent, Map<String, List<DeptDTO>> childrenMap) {
        List<DeptDTO> children = childrenMap.get(parent.getDeptId());
        if (children != null) {
            children.sort(Comparator.comparingInt(d -> d.getSortOrder() == null ? 0 : d.getSortOrder()));
            parent.setChildren(children);
            for (DeptDTO child : children) {
                buildChildren(child, childrenMap);
            }
        }
    }

    private boolean hasChildren(String deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId)
                .eq(SysDept::getDeleted, Deleted.NO.getValue());
        return deptMapper.selectCount(wrapper) > 0;
    }

    private SysDept selectByIdNotDeleted(String deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeptId, deptId)
                .eq(SysDept::getDeleted, Deleted.NO.getValue());
        SysDept entity = deptMapper.selectOne(wrapper);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "部门不存在");
        }
        return entity;
    }
}
