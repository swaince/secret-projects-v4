package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.service.UidService;
import com.dfec.soft.secret.system.entity.SysAuthorization;
import com.dfec.soft.secret.system.mapper.AuthorizationMapper;
import com.dfec.soft.secret.system.service.AuthorizationService;
import com.dfec.soft.secret.system.service.UserRelationService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单授权服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

    private final AuthorizationMapper authorizationMapper;
    private final UserRelationService userRelationService;
    private final UidService uidService;

    public AuthorizationServiceImpl(AuthorizationMapper authorizationMapper, UserRelationService userRelationService,
            UidService uidService) {
        this.authorizationMapper = authorizationMapper;
        this.userRelationService = userRelationService;
        this.uidService = uidService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(String subjectType, List<String> subjectIds, List<String> menuIds, String createdBy) {
        String type = subjectType.toUpperCase();
        LambdaQueryWrapper<SysAuthorization> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysAuthorization::getSubjectType, type)
                .in(SysAuthorization::getSubjectId, subjectIds);
        authorizationMapper.delete(deleteWrapper);

        LocalDateTime now = LocalDateTime.now();
        List<SysAuthorization> entities = new ArrayList<>();
        for (String subjectId : subjectIds) {
            for (String menuId : menuIds) {
                SysAuthorization entity = new SysAuthorization();
                entity.setAuthId(uidService.next());
                entity.setSubjectType(type);
                entity.setSubjectId(subjectId);
                entity.setMenuId(menuId);
                entity.setCreatedAt(now);
                entity.setCreatedBy(createdBy);
                entities.add(entity);
            }
        }

        for (SysAuthorization entity : entities) {
            authorizationMapper.insert(entity);
        }

        LOGGER.info("批量保存菜单授权，subjectType={}, subjectIds={}, menuIds={}",
                type, subjectIds.size(), menuIds.size());
    }

    private static final String ROLE = "ROLE";
    private static final String POST = "POST";
    private static final String DEPT = "DEPT";
    private static final String USER = "USER";

    @Override
    public List<String> getMenuIds(String subjectType, String subjectId) {
        LambdaQueryWrapper<SysAuthorization> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(SysAuthorization::getMenuId)
                .eq(SysAuthorization::getSubjectType, subjectType.toUpperCase())
                .eq(SysAuthorization::getSubjectId, subjectId);
        return authorizationMapper.selectList(wrapper).stream()
                .map(SysAuthorization::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllMenuIdsForUser(String userId) {
        Set<String> menuIds = new HashSet<>();

        menuIds.addAll(getMenuIds(USER, userId));

        for (String relationType : List.of(ROLE, POST, DEPT)) {
            List<String> targetIds = userRelationService.getTargetIds(relationType, userId);
            for (String targetId : targetIds) {
                menuIds.addAll(getMenuIds(relationType, targetId));
            }
        }

        LOGGER.info("查询用户所有权限，userId={}, menuIds={}", userId, menuIds.size());
        return new ArrayList<>(menuIds);
    }
}
