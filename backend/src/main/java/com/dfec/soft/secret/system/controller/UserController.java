package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.annotation.TokenParam;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.system.dto.common.UserDTO;
import com.dfec.soft.secret.system.dto.request.UserPageRequest;
import com.dfec.soft.secret.system.service.UserService;
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
 * 用户管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户。
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    @GetMapping
    public R<PageResponse<UserDTO>> page(@Valid UserPageRequest request) {
        return R.ok(userService.page(request));
    }

    /**
     * 创建用户。
     *
     * @param request 创建请求
     * @param userId  当前用户 ID
     * @return 创建的用户
     */
    @PostMapping
    public R<UserDTO> create(@Validated(Create.class) @Valid @RequestBody UserDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(userService.create(request, userId));
    }

    /**
     * 更新用户。
     *
     * @param userId  用户 ID
     * @param request 更新请求
     * @param currentUserId 当前用户 ID
     * @return 更新后的用户
     */
    @PutMapping("/{userId}")
    public R<UserDTO> update(@PathVariable String userId, @RequestBody UserDTO request,
            @TokenParam("userId") String currentUserId) {
        return R.ok(userService.update(userId, request, currentUserId));
    }

    /**
     * 根据 ID 删除用户。
     *
     * @param userId  用户 ID
     * @param currentUserId 当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping("/{userId}")
    public R<String> deleteById(@PathVariable String userId, @TokenParam("userId") String currentUserId) {
        return R.ok(userService.deleteById(userId, currentUserId));
    }

    /**
     * 批量删除用户。
     *
     * @param ids    用户 ID 列表
     * @param userId 当前用户 ID
     * @return 被删除的 ID 列表
     */
    @DeleteMapping
    public R<List<String>> delete(@NotEmpty @RequestBody List<String> ids,
            @TokenParam("userId") String userId) {
        return R.ok(userService.delete(ids, userId));
    }
}
