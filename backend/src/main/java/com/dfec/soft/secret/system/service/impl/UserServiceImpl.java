package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.constants.Deleted;
import com.dfec.soft.secret.common.constants.Status;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.common.service.UidService;
import com.dfec.soft.secret.system.dto.common.UserDTO;
import com.dfec.soft.secret.system.dto.request.UserPageRequest;
import com.dfec.soft.secret.system.entity.SysUser;
import com.dfec.soft.secret.system.mapper.UserMapper;
import com.dfec.soft.secret.system.mapstruct.UserStructMapper;
import com.dfec.soft.secret.system.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 用户服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
@Validated
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final UserStructMapper userStructMapper;
    private final UidService uidService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserMapper userMapper, UserStructMapper userStructMapper, UidService uidService) {
        this.userMapper = userMapper;
        this.userStructMapper = userStructMapper;
        this.uidService = uidService;
    }

    /**
     * 分页查询用户列表。
     *
     * @param request 分页请求参数
     * @return 用户分页响应
     */
    @Override
    public PageResponse<UserDTO> page(UserPageRequest request) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleted, Deleted.NO.getValue());
        if (StringUtils.isNotBlank(request.getUsername())) {
            wrapper.like(SysUser::getUsername, request.getUsername());
        }
        if (StringUtils.isNotBlank(request.getDisplayName())) {
            wrapper.like(SysUser::getDisplayName, request.getDisplayName());
        }
        if (StringUtils.isNotBlank(request.getGender())) {
            wrapper.eq(SysUser::getGender, request.getGender());
        }
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }
        long total = userMapper.selectCount(wrapper);
        wrapper.orderByDesc(SysUser::getCreatedAt);
        Page<SysUser> pageParam = new Page<>(request.getPage(), request.getSize(), false);
        Page<SysUser> pageResult = userMapper.selectPage(pageParam, wrapper);
        pageResult.setTotal(total);
        LOGGER.info("page={}, size={}, total={}", request.getPage(), request.getSize(), total);
        return PageResponse.of(userStructMapper.entityToDTO(pageResult.getRecords()), pageResult);
    }

    /**
     * 创建用户。
     *
     * @param request   用户创建请求
     * @param createdBy 创建人
     * @return 创建后的用户 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO create(UserDTO request, String createdBy) {
        LambdaQueryWrapper<SysUser> usernameCheck = new LambdaQueryWrapper<>();
        usernameCheck.eq(SysUser::getUsername, request.getUsername())
                .eq(SysUser::getDeleted, Deleted.NO.getValue());
        if (userMapper.selectCount(usernameCheck) > 0) {
            throw new OuterException(BizCode.CONFLICT, "用户名已存在");
        }
        SysUser entity = userStructMapper.requestToEntity(request);
        entity.setUserId(uidService.next());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setStatus(Status.ENABLED.getValue());
        entity.setDeleted(Deleted.NO.getValue());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(createdBy);
        userMapper.insert(entity);
        LOGGER.info("创建用户，userId={}, username={}", entity.getUserId(), entity.getUsername());
        return userStructMapper.entityToDTO(entity);
    }

    /**
     * 更新用户。
     *
     * @param userId    用户 ID
     * @param request   用户更新请求
     * @param updatedBy 更新人
     * @return 更新后的用户 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO update(String userId, UserDTO request, String updatedBy) {
        SysUser entity = selectByIdNotDeleted(userId);
        userStructMapper.updateEntity(entity, request);
        if (StringUtils.isNotBlank(request.getPassword())) {
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
        LOGGER.info("更新用户，userId={}", userId);
        return userStructMapper.entityToDTO(entity);
    }

    /**
     * 根据 ID 删除用户（逻辑删除）。
     *
     * @param userId    用户 ID
     * @param updatedBy 操作人
     * @return 被删除的用户 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(String userId, String updatedBy) {
        SysUser entity = selectByIdNotDeleted(userId);
        entity.setDeleted(Deleted.YES.getValue());
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
        LOGGER.info("删除用户，userId={}", userId);
        return userId;
    }

    /**
     * 批量删除用户。
     *
     * @param ids       用户 ID 列表
     * @param updatedBy 操作人
     * @return 被删除的用户 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> delete(List<String> ids, String updatedBy) {
        for (String id : ids) {
            deleteById(id, updatedBy);
        }
        return ids;
    }

    private SysUser selectByIdNotDeleted(String userId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserId, userId)
                .eq(SysUser::getDeleted, Deleted.NO.getValue());
        SysUser entity = userMapper.selectOne(wrapper);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "用户不存在");
        }
        return entity;
    }
}
