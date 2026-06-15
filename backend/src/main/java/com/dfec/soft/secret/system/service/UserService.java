package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.system.dto.common.UserDTO;
import com.dfec.soft.secret.system.dto.request.UserPageRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

/**
 * 用户服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 分页查询用户。
     *
     * @param request 分页请求参数
     * @return 用户分页响应
     */
    PageResponse<UserDTO> page(@Valid UserPageRequest request);

    /**
     * 创建用户。
     *
     * @param request   创建请求
     * @param createdBy 创建人
     * @return 创建后的用户 DTO
     */
    UserDTO create(@Validated(Create.class) @Valid UserDTO request, String createdBy);

    /**
     * 更新用户。
     *
     * @param userId    用户 ID
     * @param request   更新请求
     * @param updatedBy 操作人
     * @return 更新后的用户 DTO
     */
    UserDTO update(String userId, UserDTO request, String updatedBy);

    /**
     * 根据 ID 删除用户。
     *
     * @param userId    用户 ID
     * @param updatedBy 操作人
     * @return 被删除的用户 ID
     */
    String deleteById(String userId, String updatedBy);

    /**
     * 批量删除用户。
     *
     * @param ids       用户 ID 列表
     * @param updatedBy 操作人
     * @return 被删除的用户 ID 列表
     */
    List<String> delete(List<String> ids, String updatedBy);
}
