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
import com.dfec.soft.secret.system.dto.common.PostDTO;
import com.dfec.soft.secret.system.dto.request.PostPageRequest;
import com.dfec.soft.secret.system.entity.SysPost;
import com.dfec.soft.secret.system.mapper.PostMapper;
import com.dfec.soft.secret.system.mapstruct.PostStructMapper;
import com.dfec.soft.secret.system.service.PostService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 岗位服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
@Validated
public class PostServiceImpl implements PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostMapper postMapper;
    private final PostStructMapper postStructMapper;
    private final UidService uidService;

    public PostServiceImpl(PostMapper postMapper, PostStructMapper postStructMapper, UidService uidService) {
        this.postMapper = postMapper;
        this.postStructMapper = postStructMapper;
        this.uidService = uidService;
    }

    /**
     * 分页查询岗位列表。
     *
     * @param request 分页请求参数
     * @return 岗位分页响应
     */
    @Override
    public PageResponse<PostDTO> page(PostPageRequest request) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getDeleted, Deleted.NO.getValue());
        if (StringUtils.isNotBlank(request.getPostName())) {
            wrapper.like(SysPost::getPostName, request.getPostName());
        }
        if (StringUtils.isNotBlank(request.getPostCode())) {
            wrapper.like(SysPost::getPostCode, request.getPostCode());
        }
        if (request.getPostLevel() != null) {
            wrapper.eq(SysPost::getPostLevel, request.getPostLevel());
        }
        long total = postMapper.selectCount(wrapper);
        wrapper.orderByAsc(SysPost::getSortOrder);
        Page<SysPost> pageParam = new Page<>(request.getPage(), request.getSize(), false);
        Page<SysPost> pageResult = postMapper.selectPage(pageParam, wrapper);
        pageResult.setTotal(total);
        LOGGER.info("page={}, size={}, total={}", request.getPage(), request.getSize(), total);
        return PageResponse.of(postStructMapper.entityToDTO(pageResult.getRecords()), pageResult);
    }

    /**
     * 根据 ID 查询岗位。
     *
     * @param postId 岗位 ID
     * @return 岗位 DTO
     */
    @Override
    public PostDTO getById(String postId) {
        SysPost entity = selectByIdNotDeleted(postId);
        return postStructMapper.entityToDTO(entity);
    }

    /**
     * 创建岗位。
     *
     * @param request   岗位创建请求
     * @param createdBy 创建人
     * @return 创建后的岗位 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostDTO create(PostDTO request, String createdBy) {
        LambdaQueryWrapper<SysPost> codeCheck = new LambdaQueryWrapper<>();
        codeCheck.eq(SysPost::getPostCode, request.getPostCode())
                .eq(SysPost::getDeleted, Deleted.NO.getValue());
        if (postMapper.selectCount(codeCheck) > 0) {
            throw new OuterException(BizCode.CONFLICT, "岗位编码已存在");
        }
        SysPost entity = postStructMapper.requestToEntity(request);
        entity.setPostId(uidService.next());
        if (entity.getSortOrder() == null || entity.getSortOrder() == 0) {
            long count = postMapper.selectCount(new LambdaQueryWrapper<SysPost>()
                    .eq(SysPost::getDeleted, Deleted.NO.getValue()));
            entity.setSortOrder((int) count + 1);
        }
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(createdBy);
        postMapper.insert(entity);
        LOGGER.info("创建岗位，postId={}, postCode={}", entity.getPostId(), entity.getPostCode());
        return postStructMapper.entityToDTO(entity);
    }

    /**
     * 更新岗位。
     *
     * @param postId    岗位 ID
     * @param request   岗位更新请求
     * @param updatedBy 更新人
     * @return 更新后的岗位 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostDTO update(String postId, PostDTO request, String updatedBy) {
        SysPost entity = selectByIdNotDeleted(postId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置岗位不可修改");
        }
        postStructMapper.updateEntity(entity, request);
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(entity);
        LOGGER.info("更新岗位，postId={}", postId);
        return postStructMapper.entityToDTO(entity);
    }

    /**
     * 批量删除岗位，跳过内置岗位。
     *
     * @param postIds 岗位 ID 列表
     * @param userId  操作用户 ID
     * @return 被删除的岗位 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> delete(List<String> postIds, String userId) {
        List<String> deleted = new ArrayList<>();
        for (String postId : postIds) {
            SysPost entity = selectByIdNotDeleted(postId);
            if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
                LOGGER.info("跳过内置岗位，postId={}", postId);
                continue;
            }
            entity.setUpdatedBy(userId);
            entity.setUpdatedAt(LocalDateTime.now());
            postMapper.updateById(entity);
            postMapper.deleteById(postId);
            deleted.add(postId);
        }
        return deleted;
    }

    /**
     * 根据 ID 删除岗位。
     *
     * @param postId 岗位 ID
     * @param userId 操作用户 ID
     * @return 被删除的岗位 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteById(String postId, String userId) {
        SysPost entity = selectByIdNotDeleted(postId);
        if (entity.getBuiltIn() != null && entity.getBuiltIn().equals(Builtin.YES.getValue())) {
            throw new OuterException(BizCode.FORBIDDEN, "内置岗位不可删除");
        }
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(entity);
        postMapper.deleteById(postId);
        LOGGER.info("删除岗位，postId={}", postId);
        return postId;
    }

    private SysPost selectByIdNotDeleted(String postId) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getPostId, postId)
                .eq(SysPost::getDeleted, Deleted.NO.getValue());
        SysPost entity = postMapper.selectOne(wrapper);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "岗位不存在");
        }
        return entity;
    }
}
