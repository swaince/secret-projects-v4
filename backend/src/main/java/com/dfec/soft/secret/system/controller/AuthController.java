package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.annotation.TokenParam;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.AuthSaveDTO;
import com.dfec.soft.secret.system.service.AuthorizationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单授权管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/authorizations")
public class AuthController {

    private final AuthorizationService authorizationService;

    public AuthController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    /**
     * 批量保存菜单授权。
     *
     * @param subjectType 主体类型
     * @param request     保存请求
     * @param userId      当前用户 ID
     * @return 操作结果
     */
    @PostMapping("/{subjectType}")
    public R<Void> save(@PathVariable String subjectType, @RequestBody AuthSaveDTO request,
            @TokenParam("userId") String userId) {
        authorizationService.saveBatch(subjectType, request.getSubjectIds(), request.getMenuIds(), userId);
        return R.ok();
    }

    /**
     * 查询主体的已授权菜单 ID 列表。
     *
     * @param subjectType 主体类型
     * @param subjectId   主体 ID
     * @return 菜单 ID 列表
     */
    @GetMapping("/{subjectType}/{subjectId}")
    public R<List<String>> getMenuIds(@PathVariable String subjectType, @PathVariable String subjectId) {
        return R.ok(authorizationService.getMenuIds(subjectType, subjectId));
    }

    /**
     * 查询用户所有权限（直接授权 + 角色/岗位/部门间接授权汇总）。
     *
     * @param userId 用户 ID
     * @return 菜单 ID 列表
     */
    @GetMapping("/user/{userId}/all")
    public R<List<String>> getAllMenuIdsForUser(@PathVariable String userId) {
        return R.ok(authorizationService.getAllMenuIdsForUser(userId));
    }
}
