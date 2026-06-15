package com.dfec.soft.secret.system.controller;

import com.dfec.soft.secret.common.annotation.TokenParam;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.system.dto.common.MenuDTO;
import com.dfec.soft.secret.system.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单管理接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestController
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 查询菜单树。
     *
     * @return 树形菜单列表
     */
    @GetMapping("/tree")
    public R<List<MenuDTO>> tree() {
        return R.ok(menuService.tree());
    }

    /**
     * 查询菜单列表（平铺）。
     *
     * @return 菜单列表
     */
    @GetMapping
    public R<List<MenuDTO>> list() {
        return R.ok(menuService.list());
    }

    /**
     * 创建菜单。
     *
     * @param request 创建请求
     * @param userId  当前用户 ID
     * @return 创建的菜单
     */
    @PostMapping
    public R<MenuDTO> create(@Valid @RequestBody MenuDTO request, @TokenParam("userId") String userId) {
        return R.ok(menuService.create(request, userId));
    }

    /**
     * 更新菜单。
     *
     * @param menuId  菜单 ID
     * @param request 更新请求
     * @param userId  当前用户 ID
     * @return 更新后的菜单
     */
    @PutMapping("/{menuId}")
    public R<MenuDTO> update(@PathVariable String menuId, @RequestBody MenuDTO request,
            @TokenParam("userId") String userId) {
        return R.ok(menuService.update(menuId, request, userId));
    }

    /**
     * 删除菜单（级联删除子菜单）。
     *
     * @param menuId 菜单 ID
     * @param userId 当前用户 ID
     * @return 被删除的菜单 ID
     */
    @DeleteMapping("/{menuId}")
    public R<String> delete(@PathVariable String menuId, @TokenParam("userId") String userId) {
        return R.ok(menuService.delete(menuId, userId));
    }
}
