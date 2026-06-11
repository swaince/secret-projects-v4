package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.DictItemDTO;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.service.DictItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.dfec.soft.secret.common.annotation.TokenParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典项管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/dicts/{dictId}/items")
public class DictItemController {

    private final DictItemService dictItemService;

    public DictItemController(DictItemService dictItemService) {
        this.dictItemService = dictItemService;
    }

    /**
     * 根据字典 ID 查询字典项列表。
     *
     * @param dictId 字典 ID
     * @return 字典项列表
     */
    @GetMapping
    public R<List<DictItemDTO>> listByDictId(@PathVariable String dictId) {
        return R.ok(dictItemService.listByDictId(dictId));
    }

    /**
     * 创建字典项。
     *
     * @param dictId  所属字典 ID
     * @param request 创建请求
     * @param userId  当前用户 ID
     * @return 创建的字典项
     */
    @PostMapping
    public R<DictItemDTO> create(@PathVariable String dictId,
            @Validated(Create.class) @Valid @RequestBody DictItemDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(dictItemService.create(dictId, request, userId));
    }

    /**
     * 更新字典项。
     *
     * @param itemId  字典项 ID
     * @param request 更新请求
     * @param userId  当前用户 ID
     * @return 更新后的字典项
     */
    @PutMapping("/{itemId}")
    public R<DictItemDTO> update(@PathVariable String itemId,
            @Validated(Update.class) @Valid @RequestBody DictItemDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(dictItemService.update(itemId, request, userId));
    }

    /**
     * 批量删除字典项。
     *
     * @param itemIds 字典项 ID 列表
     * @param userId  当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping
    public R<List<String>> delete(@NotEmpty @RequestBody List<String> itemIds,
            @TokenParam("userId") String userId) {
        return R.ok(dictItemService.delete(itemIds, userId));
    }

    /**
     * 根据 ID 删除字典项。
     *
     * @param itemId 字典项 ID
     * @param userId 当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping("/{itemId}")
    public R<String> deleteById(@PathVariable String itemId,
            @TokenParam("userId") String userId) {
        return R.ok(dictItemService.deleteById(itemId, userId));
    }
}
