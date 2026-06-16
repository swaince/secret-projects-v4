package com.dfec.soft.secret.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dfec.soft.secret.system.entity.SysAuthorization;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单授权 Mapper。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper
public interface AuthorizationMapper extends BaseMapper<SysAuthorization> {
}
