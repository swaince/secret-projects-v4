package com.dfec.soft.secret.system.mapstruct;

import com.dfec.soft.secret.system.dto.common.PostDTO;
import com.dfec.soft.secret.system.entity.SysPost;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * 岗位 MapStruct 映射器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface PostStructMapper {

    /**
     * entity → dto。
     *
     * @param entity 实体
     * @return DTO
     */
    PostDTO entityToDTO(SysPost entity);

    /**
     * entity list → dto list。
     *
     * @param list 实体列表
     * @return DTO 列表
     */
    List<PostDTO> entityToDTO(List<SysPost> list);

    /**
     * dto → entity。
     *
     * @param request DTO 请求
     * @return 实体
     */
    SysPost requestToEntity(PostDTO request);

    /**
     * 更新实体。
     *
     * @param entity  实体
     * @param request DTO 请求
     */
    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "builtIn", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(@MappingTarget SysPost entity, PostDTO request);
}
