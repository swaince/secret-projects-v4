package com.dfec.soft.secret.system.mapstruct;

import com.dfec.soft.secret.system.dto.common.DictDTO;
import com.dfec.soft.secret.system.entity.SysDict;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * 字典 MapStruct 映射器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DictStructMapper {

    /**
     * entity → dto。
     *
     * @param entity 实体
     * @return DTO
     */
    DictDTO entityToDTO(SysDict entity);

    /**
     * entity list → dto list。
     *
     * @param list 实体列表
     * @return DTO 列表
     */
    List<DictDTO> entityToDTO(List<SysDict> list);

    /**
     * dto → entity。
     *
     * @param request DTO 请求
     * @return 实体
     */
    SysDict requestToEntity(DictDTO request);

    /**
     * 更新实体。
     *
     * @param entity  实体
     * @param request DTO 请求
     */
    @Mapping(target = "dictId", ignore = true)
    @Mapping(target = "builtIn", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(@MappingTarget SysDict entity, DictDTO request);
}
