package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.DeptDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

/**
 * 部门服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface DeptService {

    /**
     * 获取部门树。
     *
     * @return 部门树列表
     */
    List<DeptDTO> tree();

    /**
     * 根据 ID 获取部门。
     *
     * @param deptId 部门 ID
     * @return 部门 DTO
     */
    DeptDTO getById(String deptId);

    /**
     * 创建部门。
     *
     * @param request   创建请求
     * @param createdBy 创建人
     * @return 创建后的部门 DTO
     */
    DeptDTO create(@Validated(Create.class) @Valid DeptDTO request, String createdBy);

    /**
     * 更新部门。
     *
     * @param deptId    部门 ID
     * @param request   更新请求
     * @param updatedBy 操作人
     * @return 更新后的部门 DTO
     */
    DeptDTO update(String deptId, @Validated(Update.class) @Valid DeptDTO request, String updatedBy);

    /**
     * 批量删除部门。
     *
     * @param ids    部门 ID 列表
     * @param userId 操作人
     * @return 被删除的部门 ID 列表
     */
    List<String> delete(List<String> ids, String userId);

    /**
     * 根据 ID 删除部门。
     *
     * @param deptId 部门 ID
     * @param userId 操作人
     * @return 被删除的部门 ID
     */
    String deleteById(String deptId, String userId);
}
