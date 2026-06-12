package com.dfec.soft.secret.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dfec.soft.secret.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {
}
