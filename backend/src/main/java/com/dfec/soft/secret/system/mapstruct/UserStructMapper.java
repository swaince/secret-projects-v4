package com.dfec.soft.secret.system.mapstruct;

import com.dfec.soft.secret.system.dto.common.UserDTO;
import com.dfec.soft.secret.system.entity.SysUser;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 用户 MapStruct 映射器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserStructMapper {

    /**
     * entity → dto（忽略密码）。
     *
     * @param entity 实体
     * @return DTO
     */
    @Mapping(target = "password", ignore = true)
    UserDTO entityToDTO(SysUser entity);

    /**
     * entity list → dto list。
     *
     * @param list 实体列表
     * @return DTO 列表
     */
    List<UserDTO> entityToDTO(List<SysUser> list);

    /**
     * dto → entity（用于创建）。
     *
     * @param request DTO 请求
     * @return 实体
     */
    SysUser requestToEntity(UserDTO request);

    /**
     * 更新实体（忽略不可变字段和密码）。
     *
     * @param entity  实体
     * @param request DTO 请求
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(@MappingTarget SysUser entity, UserDTO request);
}
