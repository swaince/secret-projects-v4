package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.DictItemDTO;
import com.dfec.soft.secret.system.dto.common.DictWithItemsDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;

/**
 * 字典项服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface DictItemService {

    /**
     * 根据字典 ID 查询字典项列表。
     *
     * @param dictId 字典 ID
     * @return 字典项 DTO 列表
     */
    List<DictItemDTO> listByDictId(String dictId);

    /**
     * 创建字典项。
     *
     * @param dictId    所属字典 ID
     * @param request   创建请求
     * @param createdBy 创建人
     * @return 创建后的字典项 DTO
     */
    DictItemDTO create(String dictId, @Validated(Create.class) @Valid DictItemDTO request, String createdBy);

    /**
     * 更新字典项。
     *
     * @param itemId    字典项 ID
     * @param request   更新请求
     * @param createdBy 操作人
     * @return 更新后的字典项 DTO
     */
    DictItemDTO update(String itemId, @Validated(Update.class) @Valid DictItemDTO request, String createdBy);

    /**
     * 批量删除字典项。
     *
     * @param itemIds 字典项 ID 列表
     * @param userId  操作人
     * @return 被删除的字典项 ID 列表
     */
    List<String> delete(List<String> itemIds, String userId);

    /**
     * 根据 ID 删除字典项。
     *
     * @param itemId 字典项 ID
     * @param userId 操作人
     * @return 被删除的字典项 ID
     */
    String deleteById(String itemId, String userId);

    /**
     * 根据字典 ID 删除所有字典项。
     *
     * @param dictId 字典 ID
     * @return 被删除的字典项 ID 列表
     */
    List<String> deleteByDictId(String dictId);

    /**
     * 根据字典编码删除所有字典项。
     *
     * @param dictCode 字典编码
     * @return 被删除的字典项 ID 列表，若字典不存在则返回空列表
     */
    List<String> deleteByDictCode(String dictCode);

    /**
     * 根据字典编码查询字典及其字典项。
     *
     * @param dictCode 字典编码
     * @return 字典及字典项 DTO，若字典不存在则返回 null
     */
    DictWithItemsDTO getWithItemsByCode(String dictCode);
}
