package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.annotation.TokenParam;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.RelationSaveDTO;
import com.dfec.soft.secret.system.service.UserRelationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户关系管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/relations")
public class UserRelationController {

    private final UserRelationService userRelationService;

    public UserRelationController(UserRelationService userRelationService) {
        this.userRelationService = userRelationService;
    }

    /**
     * 批量保存用户关系。
     *
     * @param relationType 关系类型
     * @param request      保存请求
     * @param userId       当前用户 ID
     * @return 操作结果
     */
    @PostMapping("/{relationType}")
    public R<Void> save(@PathVariable String relationType, @RequestBody RelationSaveDTO request,
            @TokenParam("userId") String userId) {
        userRelationService.saveBatch(relationType, request.getUserIds(), request.getTargetIds(), userId);
        return R.ok();
    }

    /**
     * 查询用户的关系目标 ID 列表。
     *
     * @param relationType 关系类型
     * @param userId       用户 ID
     * @return 目标 ID 列表
     */
    @GetMapping("/{relationType}/{userId}")
    public R<List<String>> getTargets(@PathVariable String relationType, @PathVariable String userId) {
        return R.ok(userRelationService.getTargetIds(relationType, userId));
    }
}
