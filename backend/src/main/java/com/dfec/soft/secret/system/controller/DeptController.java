package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.annotation.TokenParam;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.common.validation.group.Create;
import com.dfec.soft.secret.common.validation.group.Update;
import com.dfec.soft.secret.system.dto.common.DeptDTO;
import com.dfec.soft.secret.system.service.DeptService;
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
 * 部门管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/depts")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    /**
     * 获取部门树。
     *
     * @return 部门树列表
     */
    @GetMapping
    public R<List<DeptDTO>> tree() {
        return R.ok(deptService.tree());
    }

    /**
     * 根据 ID 获取部门。
     *
     * @param deptId 部门 ID
     * @return 部门详情
     */
    @GetMapping("/{deptId}")
    public R<DeptDTO> getById(@PathVariable String deptId) {
        return R.ok(deptService.getById(deptId));
    }

    /**
     * 创建部门。
     *
     * @param request 创建请求
     * @param userId  当前用户 ID
     * @return 创建的部门
     */
    @PostMapping
    public R<DeptDTO> create(@Validated(Create.class) @Valid @RequestBody DeptDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(deptService.create(request, userId));
    }

    /**
     * 更新部门。
     *
     * @param deptId  部门 ID
     * @param request 更新请求
     * @param userId  当前用户 ID
     * @return 更新后的部门
     */
    @PutMapping("/{deptId}")
    public R<DeptDTO> update(@PathVariable String deptId, @Validated(Update.class) @Valid @RequestBody DeptDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(deptService.update(deptId, request, userId));
    }

    /**
     * 批量删除部门。
     *
     * @param ids    部门 ID 列表
     * @param userId 当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping
    public R<List<String>> delete(@NotEmpty @RequestBody List<String> ids,
            @TokenParam("userId") String userId) {
        return R.ok(deptService.delete(ids, userId));
    }

    /**
     * 根据 ID 删除部门。
     *
     * @param deptId 部门 ID
     * @param userId 当前用户 ID
     * @return 被删除的 ID
     */
    @DeleteMapping("/{deptId}")
    public R<String> deleteById(@PathVariable String deptId, @TokenParam("userId") String userId) {
        return R.ok(deptService.deleteById(deptId, userId));
    }
}
