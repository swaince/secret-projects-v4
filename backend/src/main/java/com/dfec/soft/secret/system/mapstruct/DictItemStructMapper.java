package com.dfec.soft.secret.system.mapstruct;

import com.dfec.soft.secret.system.dto.common.DictItemDTO;
import com.dfec.soft.secret.system.entity.SysDictItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * 字典项 MapStruct 映射器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DictItemStructMapper {

    /**
     * entity → dto。
     *
     * @param entity 实体
     * @return DTO
     */
    DictItemDTO entityToDTO(SysDictItem entity);

    /**
     * entity list → dto list。
     *
     * @param list 实体列表
     * @return DTO 列表
     */
    List<DictItemDTO> entityToDTO(List<SysDictItem> list);

    /**
     * dto → entity。
     *
     * @param request DTO 请求
     * @return 实体
     */
    SysDictItem requestToEntity(DictItemDTO request);

    /**
     * 更新实体。
     *
     * @param entity  实体
     * @param request DTO 请求
     */
    void updateEntity(@MappingTarget SysDictItem entity, DictItemDTO request);
}
