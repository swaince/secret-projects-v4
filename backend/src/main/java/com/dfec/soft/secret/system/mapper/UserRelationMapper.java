package com.dfec.soft.secret.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dfec.soft.secret.system.entity.SysUserRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关系 Mapper。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper
public interface UserRelationMapper extends BaseMapper<SysUserRelation> {
}
