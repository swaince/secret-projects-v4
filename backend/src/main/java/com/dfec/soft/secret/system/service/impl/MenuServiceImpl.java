package com.dfec.soft.secret.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.constants.Deleted;
import com.dfec.soft.secret.common.constants.Status;
import com.dfec.soft.secret.common.exception.OuterException;
import com.dfec.soft.secret.common.service.UidService;
import com.dfec.soft.secret.system.dto.common.MenuDTO;
import com.dfec.soft.secret.system.entity.SysMenu;
import com.dfec.soft.secret.system.mapper.MenuMapper;
import com.dfec.soft.secret.system.mapstruct.MenuStructMapper;
import com.dfec.soft.secret.system.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 菜单服务实现。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Service
@Validated
public class MenuServiceImpl implements MenuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final MenuMapper menuMapper;
    private final MenuStructMapper menuStructMapper;
    private final UidService uidService;

    public MenuServiceImpl(MenuMapper menuMapper, MenuStructMapper menuStructMapper, UidService uidService) {
        this.menuMapper = menuMapper;
        this.menuStructMapper = menuStructMapper;
        this.uidService = uidService;
    }

    /**
     * 查询菜单树（仅启用且未删除的菜单）。
     *
     * @return 树形菜单列表
     */
    @Override
    public List<MenuDTO> tree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getDeleted, Deleted.NO.getValue())
                .eq(SysMenu::getStatus, Status.ENABLED.getValue())
                .orderByAsc(SysMenu::getSortOrder);
        List<SysMenu> allMenus = menuMapper.selectList(wrapper);
        List<MenuDTO> dtoList = menuStructMapper.entityToDTO(allMenus);
        return buildTree(dtoList);
    }

    /**
     * 查询菜单平铺列表。
     *
     * @return 菜单列表
     */
    @Override
    public List<MenuDTO> list() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getDeleted, Deleted.NO.getValue())
                .orderByAsc(SysMenu::getSortOrder);
        List<SysMenu> allMenus = menuMapper.selectList(wrapper);
        return menuStructMapper.entityToDTO(allMenus);
    }

    /**
     * 创建菜单。
     *
     * @param request   菜单请求
     * @param createdBy 创建人
     * @return 创建后的菜单 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuDTO create(MenuDTO request, String createdBy) {
        SysMenu entity = menuStructMapper.requestToEntity(request);
        entity.setMenuId(uidService.next());
        entity.setStatus(Status.ENABLED.getValue());
        entity.setDeleted(Deleted.NO.getValue());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(createdBy);
        menuMapper.insert(entity);
        LOGGER.info("创建菜单，menuId={}, menuName={}", entity.getMenuId(), entity.getMenuName());
        return menuStructMapper.entityToDTO(entity);
    }

    /**
     * 更新菜单。
     *
     * @param menuId    菜单 ID
     * @param request   菜单请求
     * @param updatedBy 操作人
     * @return 更新后的菜单 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuDTO update(String menuId, MenuDTO request, String updatedBy) {
        SysMenu entity = selectByIdNotDeleted(menuId);
        if (menuId.equals(request.getParentId())) {
            throw new OuterException(BizCode.BAD_REQUEST, "父菜单不能是自身");
        }
        menuStructMapper.updateEntity(entity, request);
        entity.setUpdatedBy(updatedBy);
        entity.setUpdatedAt(LocalDateTime.now());
        menuMapper.updateById(entity);
        LOGGER.info("更新菜单，menuId={}", menuId);
        return menuStructMapper.entityToDTO(entity);
    }

    /**
     * 删除菜单（级联删除所有子菜单）。
     *
     * @param menuId    菜单 ID
     * @param updatedBy 操作人
     * @return 被删除的菜单 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(String menuId, String updatedBy) {
        selectByIdNotDeleted(menuId);
        List<String> allIds = findDescendantIds(menuId);
        allIds.add(menuId);
        LocalDateTime now = LocalDateTime.now();
        LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SysMenu::getMenuId, allIds)
                .set(SysMenu::getDeleted, Deleted.YES.getValue())
                .set(SysMenu::getUpdatedBy, updatedBy)
                .set(SysMenu::getUpdatedAt, now);
        menuMapper.update(null, updateWrapper);
        LOGGER.info("删除菜单（级联），menuId={}, 影响数量={}", menuId, allIds.size());
        return menuId;
    }

    private SysMenu selectByIdNotDeleted(String menuId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuId, menuId)
                .eq(SysMenu::getDeleted, Deleted.NO.getValue());
        SysMenu entity = menuMapper.selectOne(wrapper);
        if (entity == null) {
            throw new OuterException(BizCode.NOT_FOUND, "菜单不存在");
        }
        return entity;
    }

    /**
     * 广度优先查找所有后代菜单 ID（单次查询 + 内存 BFS，避免 N+1）。
     *
     * @param parentId 父菜单 ID
     * @return 后代菜单 ID 列表
     */
    private List<String> findDescendantIds(String parentId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getDeleted, Deleted.NO.getValue());
        List<SysMenu> allMenus = menuMapper.selectList(wrapper);

        Map<String, List<SysMenu>> parentMap = allMenus.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        List<String> result = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(parentId);
        while (!queue.isEmpty()) {
            String currentParentId = queue.poll();
            List<SysMenu> children = parentMap.getOrDefault(currentParentId, List.of());
            for (SysMenu child : children) {
                result.add(child.getMenuId());
                queue.add(child.getMenuId());
            }
        }
        return result;
    }

    /**
     * 将平铺列表构建为树形结构，按 sortOrder 排序。
     */
    private List<MenuDTO> buildTree(List<MenuDTO> dtoList) {
        Map<String, List<MenuDTO>> parentMap = dtoList.stream()
                .collect(Collectors.groupingBy(dto -> dto.getParentId() == null ? "" : dto.getParentId()));
        for (MenuDTO dto : dtoList) {
            List<MenuDTO> children = parentMap.get(dto.getMenuId());
            if (children != null) {
                children.sort(Comparator.comparingInt(m -> m.getSortOrder() == null ? 0 : m.getSortOrder()));
                dto.setChildren(children);
            }
        }
        List<MenuDTO> roots = parentMap.getOrDefault("", new ArrayList<>());
        roots.sort(Comparator.comparingInt(m -> m.getSortOrder() == null ? 0 : m.getSortOrder()));
        return roots;
    }
}
