package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.service.UidService;
import com.dfec.soft.secret.system.entity.SysUserRelation;
import com.dfec.soft.secret.system.mapper.UserRelationMapper;
import com.dfec.soft.secret.system.service.UserRelationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户关系服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
public class UserRelationServiceImpl implements UserRelationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRelationServiceImpl.class);

    private final UserRelationMapper userRelationMapper;
    private final UidService uidService;

    public UserRelationServiceImpl(UserRelationMapper userRelationMapper, UidService uidService) {
        this.userRelationMapper = userRelationMapper;
        this.uidService = uidService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(String relationType, List<String> userIds, String targetId, String createdBy) {
        LambdaQueryWrapper<SysUserRelation> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRelation::getRelationType, relationType)
                .in(SysUserRelation::getUserId, userIds);
        userRelationMapper.delete(deleteWrapper);

        LocalDateTime now = LocalDateTime.now();
        List<SysUserRelation> entities = userIds.stream()
                .map(userId -> {
                    SysUserRelation entity = new SysUserRelation();
                    entity.setRelationId(uidService.next());
                    entity.setRelationType(relationType);
                    entity.setUserId(userId);
                    entity.setTargetId(targetId);
                    entity.setCreatedAt(now);
                    entity.setCreatedBy(createdBy);
                    return entity;
                })
                .collect(Collectors.toList());

        for (SysUserRelation entity : entities) {
            userRelationMapper.insert(entity);
        }

        LOGGER.info("批量保存用户关系，relationType={}, userIds={}, targetId={}", relationType, userIds.size(), targetId);
    }

    @Override
    public List<String> getTargetIds(String relationType, String userId) {
        LambdaQueryWrapper<SysUserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysUserRelation::getTargetId)
                .eq(SysUserRelation::getRelationType, relationType)
                .eq(SysUserRelation::getUserId, userId);
        return userRelationMapper.selectList(wrapper).stream()
                .map(SysUserRelation::getTargetId)
                .collect(Collectors.toList());
    }
}
