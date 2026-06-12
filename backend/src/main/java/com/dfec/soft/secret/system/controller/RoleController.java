package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.annotation.TokenParam;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.RoleDTO;
import com.dfec.soft.secret.system.dto.request.RolePageRequest;
import com.dfec.soft.secret.system.service.RoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 分页查询角色。
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    @GetMapping
    public R<PageResponse<RoleDTO>> page(@Valid RolePageRequest request) {
        return R.ok(roleService.page(request));
    }

    /**
     * 根据 ID 获取角色。
     *
     * @param roleId 角色 ID
     * @return 角色详情
     */
    @GetMapping("/{roleId}")
    public R<RoleDTO> getById(@PathVariable String roleId) {
        return R.ok(roleService.getById(roleId));
    }

    /**
     * 创建角色。
     *
     * @param request 创建请求
     * @param userId  当前用户 ID
     * @return 创建的角色
     */
    @PostMapping
    public R<RoleDTO> create(@Validated(Create.class) @Valid @RequestBody RoleDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(roleService.create(request, userId));
    }

    /**
     * 更新角色。
     *
     * @param roleId  角色 ID
     * @param request 更新请求
     * @param userId  当前用户 ID
     * @return 更新后的角色
     */
    @PutMapping("/{roleId}")
    public R<RoleDTO> update(@PathVariable String roleId, @Validated(Update.class) @Valid @RequestBody RoleDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(roleService.update(roleId, request, userId));
    }

    /**
     * 批量删除角色。
     *
     * @param roleIds 角色 ID 列表
     * @param userId  当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping
    public R<List<String>> delete(@NotEmpty @RequestBody List<String> roleIds,
            @TokenParam("userId") String userId) {
        return R.ok(roleService.delete(roleIds, userId));
    }

    /**
     * 根据 ID 删除角色。
     *
     * @param roleId 角色 ID
     * @param userId 当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping("/{roleId}")
    public R<String> deleteById(@PathVariable String roleId, @TokenParam("userId") String userId) {
        return R.ok(roleService.deleteById(roleId, userId));
    }
}
