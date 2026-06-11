package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.common.dto.common.PageResponse;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.request.DictPageRequest;
import com.dfec.soft.secret.system.service.DictService;
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
import com.dfec.soft.secret.common.annotation.TokenParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/dicts")
public class DictController {

    private final DictService dictService;

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    /**
     * 分页查询字典。
     *
     * @param request 分页请求参数
     * @return 分页结果
     */
    @GetMapping
    public R<PageResponse<DictDTO>> page(@Valid DictPageRequest request) {
        return R.ok(dictService.page(request));
    }

    /**
     * 根据 ID 获取字典。
     *
     * @param dictId 字典 ID
     * @return 字典详情
     */
    @GetMapping("/{dictId}")
    public R<DictDTO> getById(@PathVariable String dictId) {
        return R.ok(dictService.getById(dictId));
    }

    /**
     * 创建字典。
     *
     * @param request 创建请求
     * @param userId  当前用户 ID
     * @return 创建的字典
     */
    @PostMapping
    public R<DictDTO> create(@Validated(Create.class) @Valid @RequestBody DictDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(dictService.create(request, userId));
    }

    /**
     * 更新字典。
     *
     * @param dictId  字典 ID
     * @param request 更新请求
     * @param userId  当前用户 ID
     * @return 更新后的字典
     */
    @PutMapping("/{dictId}")
    public R<DictDTO> update(@PathVariable String dictId, @Validated(Update.class) @Valid @RequestBody DictDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(dictService.update(dictId, request, userId));
    }

    /**
     * 批量删除字典及其字典项。
     *
     * @param dictIds 字典 ID 列表
     * @param userId  当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping
    public R<List<String>> delete(@NotEmpty @RequestBody List<String> dictIds,
            @TokenParam("userId") String userId) {
        return R.ok(dictService.delete(dictIds, userId));
    }

    /**
     * 根据 ID 删除字典及其字典项。
     *
     * @param dictId 字典 ID
     * @param userId 当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping("/{dictId}")
    public R<String> deleteById(@PathVariable String dictId, @TokenParam("userId") String userId) {
        return R.ok(dictService.deleteById(dictId, userId));
    }
}
