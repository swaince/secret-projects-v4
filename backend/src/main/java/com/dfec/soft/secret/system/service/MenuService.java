package com.dfec.soft.secret.system.service;

import com.dfec.soft.secret.system.dto.common.MenuDTO;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 菜单服务接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface MenuService {

    /**
     * 查询菜单树。
     *
     * @return 树形菜单列表
     */
    List<MenuDTO> tree();

    /**
     * 查询菜单平铺列表。
     *
     * @return 菜单列表
     */
    List<MenuDTO> list();

    /**
     * 创建菜单。
     *
     * @param request   菜单请求
     * @param createdBy 创建人
     * @return 创建后的菜单 DTO
     */
    MenuDTO create(@Valid MenuDTO request, String createdBy);

    /**
     * 更新菜单。
     *
     * @param menuId    菜单 ID
     * @param request   菜单请求
     * @param updatedBy 操作人
     * @return 更新后的菜单 DTO
     */
    MenuDTO update(String menuId, MenuDTO request, String updatedBy);

    /**
     * 删除菜单（级联删除子菜单）。
     *
     * @param menuId    菜单 ID
     * @param updatedBy 操作人
     * @return 被删除的菜单 ID
     */
    String delete(String menuId, String updatedBy);
}
