package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.system.dto.request.DictPageRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

/**
 * 字典服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface DictService {

    /**
     * 分页查询字典。
     *
     * @param request 分页请求参数
     * @return 字典分页响应
     */
    PageResponse<DictDTO> page(@Valid DictPageRequest request);

    /**
     * 根据 ID 获取字典。
     *
     * @param dictId 字典 ID
     * @return 字典 DTO
     */
    DictDTO getById(String dictId);

    /**
     * 创建字典。
     *
     * @param request   创建请求
     * @param createdBy 创建人
     * @return 创建后的字典 DTO
     */
    DictDTO create(@Validated(Create.class) @Valid DictDTO request, String createdBy);

    /**
     * 更新字典。
     *
     * @param dictId    字典 ID
     * @param request   更新请求
     * @param createdBy 操作人
     * @return 更新后的字典 DTO
     */
    DictDTO update(String dictId, @Validated(Update.class) @Valid DictDTO request, String createdBy);

    /**
     * 批量删除字典及其字典项。
     *
     * @param dictIds 字典 ID 列表
     * @param userId  操作人
     * @return 被删除的字典 ID 列表
     */
    List<String> delete(List<String> dictIds, String userId);

    /**
     * 根据 ID 删除字典及其字典项。
     *
     * @param dictId 字典 ID
     * @param userId 操作人
     * @return 被删除的字典 ID
     */
    String deleteById(String dictId, String userId);
}
