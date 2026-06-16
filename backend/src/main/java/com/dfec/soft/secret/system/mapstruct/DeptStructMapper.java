package com.dfec.soft.secret.system.mapstruct;

import com.dfec.soft.secret.system.dto.common.DeptDTO;
import com.dfec.soft.secret.system.entity.SysDept;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * 部门 MapStruct 映射器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DeptStructMapper {

    /**
     * entity → dto。
     *
     * @param entity 实体
     * @return DTO
     */
    DeptDTO entityToDTO(SysDept entity);

    /**
     * entity list → dto list。
     *
     * @param list 实体列表
     * @return DTO 列表
     */
    List<DeptDTO> entityToDTO(List<SysDept> list);

    /**
     * dto → entity。
     *
     * @param request DTO 请求
     * @return 实体
     */
    SysDept requestToEntity(DeptDTO request);

    /**
     * 更新实体。
     *
     * @param entity  实体
     * @param request DTO 请求
     */
    @Mapping(target = "deptId", ignore = true)
    @Mapping(target = "builtIn", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(@MappingTarget SysDept entity, DeptDTO request);
}
