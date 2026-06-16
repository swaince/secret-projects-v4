package com.dfec.soft.secret.system.mapstruct;

import com.dfec.soft.secret.system.dto.common.RoleDTO;
import com.dfec.soft.secret.system.entity.SysRole;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * 角色 MapStruct 映射器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface RoleStructMapper {

    /**
     * entity → dto。
     *
     * @param entity 实体
     * @return DTO
     */
    RoleDTO entityToDTO(SysRole entity);

    /**
     * entity list → dto list。
     *
     * @param list 实体列表
     * @return DTO 列表
     */
    List<RoleDTO> entityToDTO(List<SysRole> list);

    /**
     * dto → entity。
     *
     * @param request DTO 请求
     * @return 实体
     */
    SysRole requestToEntity(RoleDTO request);

    /**
     * 更新实体。
     *
     * @param entity  实体
     * @param request DTO 请求
     */
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "builtIn", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(@MappingTarget SysRole entity, RoleDTO request);
}
