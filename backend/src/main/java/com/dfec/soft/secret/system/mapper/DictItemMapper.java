package com.dfec.soft.secret.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dfec.soft.secret.system.entity.SysDictItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典项 Mapper。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Mapper
public interface DictItemMapper extends BaseMapper<SysDictItem> {
}
