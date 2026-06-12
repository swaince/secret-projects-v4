package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.constants.Builtin;
import com.dfec.soft.secret.common.constants.Deleted;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.common.service.UidService;
import com.dfec.soft.secret.system.dto.common.RoleDTO;
import com.dfec.soft.secret.system.dto.request.RolePageRequest;
import com.dfec.soft.secret.system.entity.SysRole;
import com.dfec.soft.secret.system.mapper.RoleMapper;
import com.dfec.soft.secret.system.mapstruct.RoleStructMapper;
import com.dfec.soft.secret.system.service.RoleService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 角色服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
@Validated
public class RoleServiceImpl implements RoleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleMapper roleMapper;
    private final RoleStructMapper roleStructMapper;
    private final UidService uidService;

    public RoleServiceImpl(RoleMapper roleMapper, RoleStructMapper roleStructMapper, UidService uidService) {
        this.roleMapper = roleMapper;
        this.roleStructMapper = roleStructMapper;
        this.uidService = uidService;
    }

    /**
     * 分页查询角色列表。
     *
     * @param request 分页请求参数
     * @return 角色分页响应
     */
    @Override
    public PageResponse<RoleDTO> page(RolePageRequest request) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getDeleted, Deleted.NO.getValue());
        if (StringUtils.isNotBlank(request.getRoleName())) {
            wrapper.like(SysRole::getRoleName, request.getRoleName());
        }
        if (StringUtils.isNotBlank(request.getRoleCode())) {
            wrapper.like(SysRole::getRoleCode, request.getRoleCode());
        }
        long total = roleMapper.selectCount(wrapper);
        wrapper.orderByAsc(SysRole::getSortOrder);
        Page<SysRole> pageParam = new Page<>(request.getPage(), request.getSize(), false);
        Page<SysRole> pageResult = roleMapper.selectPage(pageParam, wrapper);
        pageResult.setTotal(total);
        LOGGER.info("page={}, size={}, total={}", request.getPage(), request.getSize(), total);
        return PageResponse.of(roleStructMapper.entityToDTO(pageResult.getRecords()), pageResult);
    }

    /**
     * 根据 ID 查询角色。
     *
     * @param roleId 角色 ID
     * @return 角色 DTO
     */
    @Override
    public RoleDTO getById(String roleId) {
        SysRole entity = selectByIdNotDeleted(roleId);
        return roleStructMapper.entityToDTO(entity);
    }

    /**
     * 创建角色。
     *
     * @param request   角色创建请求
     * @param createdBy 创建人
     * @return 创建后的角色 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO create(RoleDTO request, String createdBy) {
        LambdaQueryWrapper<SysRole> codeCheck = new LambdaQueryWrapper<>();
        codeCheck.eq(SysRole::getRoleCode, request.getRoleCode())
                .eq(SysRole::getDeleted, Deleted.NO.getValue());
        if (roleMapper.selectCount(codeCheck) > 0) {
            throw new OuterException(BizCode.CONFLICT, "角色编码已存在");
        }
        SysRole entity = roleStructMapper.requestToEntity(request);
        entity.setRoleId(uidService.next());
        if (entity.getSortOrder() == null || entity.getSortOrder() == 0) {
            long count = roleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                    .eq(SysRole::getDeleted, Deleted.NO.getValue()));
            entity.setSortOrder((int) count + 1);
        }
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(createdBy);
        roleMapper.insert(entity);
        LOGGER.info("创建角色，roleId={}, roleCode={}", entity.getRoleId(), entity.getRoleCode());
        return roleStructMapper.entityToDTO(entity);
    }

    /**
     * 更新角色。
     *
     * @param roleId    角色 ID
     * @param request   角色更新请求
     * @param updatedBy 更新人
     * @return 更新后的角色 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO update(String roleId, RoleDTO request, String updatedBy) {
        SysRole entity = selectByIdNotDeleted(roleId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置角色不可修改");
        }
        roleStructMapper.updateEntity(entity, request);
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(entity);
        LOGGER.info("更新角色，roleId={}", roleId);
        return roleStructMapper.entityToDTO(entity);
    }

    /**
     * 批量删除角色，跳过内置角色。
     *
     * @param roleIds 角色 ID 列表
     * @param userId  操作用户 ID
     * @return 被删除的角色 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> delete(List<String> roleIds, String userId) {
        List<String> deleted = new ArrayList<>();
        for (String roleId : roleIds) {
            SysRole entity = selectByIdNotDeleted(roleId);
            if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
                LOGGER.info("跳过内置角色，roleId={}", roleId);
                continue;
            }
            entity.setUpdatedBy(userId);
            entity.setUpdatedAt(LocalDateTime.now());
            roleMapper.updateById(entity);
            roleMapper.deleteById(roleId);
            deleted.add(roleId);
        }
        return deleted;
    }

    /**
     * 根据 ID 删除角色。
     *
     * @param roleId 角色 ID
     * @param userId 操作用户 ID
     * @return 被删除的角色 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(String roleId, String userId) {
        SysRole entity = selectByIdNotDeleted(roleId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置角色不可删除");
        }
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(LocalDateTime.now());
        roleMapper.updateById(entity);
        roleMapper.deleteById(roleId);
        LOGGER.info("删除角色，roleId={}", roleId);
        return roleId;
    }

    private SysRole selectByIdNotDeleted(String roleId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleId, roleId)
                .eq(SysRole::getDeleted, Deleted.NO.getValue());
        SysRole entity = roleMapper.selectOne(wrapper);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "角色不存在");
        }
        return entity;
    }
}
