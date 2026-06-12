package com.dfec.soft.secret.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dfec.soft.secret.system.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门 Mapper。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper
public interface DeptMapper extends BaseMapper<SysDept> {
}
