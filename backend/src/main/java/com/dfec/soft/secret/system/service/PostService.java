package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.PostDTO;
import com.dfec.soft.secret.system.dto.request.PostPageRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

/**
 * 岗位服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface PostService {

    /**
     * 分页查询岗位。
     *
     * @param request 分页请求参数
     * @return 岗位分页响应
     */
    PageResponse<PostDTO> page(@Valid PostPageRequest request);

    /**
     * 根据 ID 获取岗位。
     *
     * @param postId 岗位 ID
     * @return 岗位 DTO
     */
    PostDTO getById(String postId);

    /**
     * 创建岗位。
     *
     * @param request   创建请求
     * @param createdBy 创建人
     * @return 创建后的岗位 DTO
     */
    PostDTO create(@Validated(Create.class) @Valid PostDTO request, String createdBy);

    /**
     * 更新岗位。
     *
     * @param postId    岗位 ID
     * @param request   更新请求
     * @param updatedBy 操作人
     * @return 更新后的岗位 DTO
     */
    PostDTO update(String postId, @Validated(Update.class) @Valid PostDTO request, String updatedBy);

    /**
     * 批量删除岗位。
     *
     * @param postIds 岗位 ID 列表
     * @param userId  操作人
     * @return 被删除的岗位 ID 列表
     */
    List<String> delete(List<String> postIds, String userId);

    /**
     * 根据 ID 删除岗位。
     *
     * @param postId 岗位 ID
     * @param userId 操作人
     * @return 被删除的岗位 ID
     */
    String deleteById(String postId, String userId);
}
