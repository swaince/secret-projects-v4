package com.dfec.soft.secret.system.mapstruct;

import com.dfec.soft.secret.system.dto.common.MenuDTO;
import com.dfec.soft.secret.system.entity.SysMenu;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 菜单 MapStruct 映射器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface MenuStructMapper {

    /**
     * entity → dto。
     *
     * @param entity 实体
     * @return DTO
     */
    @Mapping(target = "children", ignore = true)
    MenuDTO entityToDTO(SysMenu entity);

    /**
     * entity list → dto list。
     *
     * @param list 实体列表
     * @return DTO 列表
     */
    List<MenuDTO> entityToDTO(List<SysMenu> list);

    /**
     * dto → entity（用于创建）。
     *
     * @param request DTO 请求
     * @return 实体
     */
    SysMenu requestToEntity(MenuDTO request);

    /**
     * 更新实体（忽略不可变字段）。
     *
     * @param entity  实体
     * @param request DTO 请求
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "menuId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(@MappingTarget SysMenu entity, MenuDTO request);
}
