package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.annotation.TokenParam;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.PostDTO;
import com.dfec.soft.secret.system.dto.request.PostPageRequest;
import com.dfec.soft.secret.system.service.PostService;
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
 * 岗位管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 分页查询岗位。
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    @GetMapping
    public R<PageResponse<PostDTO>> page(@Valid PostPageRequest request) {
        return R.ok(postService.page(request));
    }

    /**
     * 根据 ID 获取岗位。
     *
     * @param postId 岗位 ID
     * @return 岗位详情
     */
    @GetMapping("/{postId}")
    public R<PostDTO> getById(@PathVariable String postId) {
        return R.ok(postService.getById(postId));
    }

    /**
     * 创建岗位。
     *
     * @param request 创建请求
     * @param userId  当前用户 ID
     * @return 创建的岗位
     */
    @PostMapping
    public R<PostDTO> create(@Validated(Create.class) @Valid @RequestBody PostDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(postService.create(request, userId));
    }

    /**
     * 更新岗位。
     *
     * @param postId  岗位 ID
     * @param request 更新请求
     * @param userId  当前用户 ID
     * @return 更新后的岗位
     */
    @PutMapping("/{postId}")
    public R<PostDTO> update(@PathVariable String postId, @Validated(Update.class) @Valid @RequestBody PostDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(postService.update(postId, request, userId));
    }

    /**
     * 批量删除岗位。
     *
     * @param postIds 岗位 ID 列表
     * @param userId  当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping
    public R<List<String>> delete(@NotEmpty @RequestBody List<String> postIds,
            @TokenParam("userId") String userId) {
        return R.ok(postService.delete(postIds, userId));
    }

    /**
     * 根据 ID 删除岗位。
     *
     * @param postId 岗位 ID
     * @param userId 当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping("/{postId}")
    public R<String> deleteById(@PathVariable String postId, @TokenParam("userId") String userId) {
        return R.ok(postService.deleteById(postId, userId));
    }
}
