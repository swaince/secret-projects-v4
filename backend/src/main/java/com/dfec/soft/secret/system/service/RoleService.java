package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.RoleDTO;
import com.dfec.soft.secret.system.dto.request.RolePageRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

/**
 * 角色服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 分页查询角色。
     *
     * @param request 分页请求参数
     * @return 角色分页响应
     */
    PageResponse<RoleDTO> page(@Valid RolePageRequest request);

    /**
     * 根据 ID 获取角色。
     *
     * @param roleId 角色 ID
     * @return 角色 DTO
     */
    RoleDTO getById(String roleId);

    /**
     * 创建角色。
     *
     * @param request   创建请求
     * @param createdBy 创建人
     * @return 创建后的角色 DTO
     */
    RoleDTO create(@Validated(Create.class) @Valid RoleDTO request, String createdBy);

    /**
     * 更新角色。
     *
     * @param roleId    角色 ID
     * @param request   更新请求
     * @param updatedBy 操作人
     * @return 更新后的角色 DTO
     */
    RoleDTO update(String roleId, @Validated(Update.class) @Valid RoleDTO request, String updatedBy);

    /**
     * 批量删除角色。
     *
     * @param roleIds 角色 ID 列表
     * @param userId  操作人
     * @return 被删除的角色 ID 列表
     */
    List<String> delete(List<String> roleIds, String userId);

    /**
     * 根据 ID 删除角色。
     *
     * @param roleId 角色 ID
     * @param userId 操作人
     * @return 被删除的角色 ID
     */
    String deleteById(String roleId, String userId);
}
