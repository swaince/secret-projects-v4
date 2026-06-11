package com.dfec.soft.secret.common.constants;

/**
 * 字典元素接口。
 *
 * @param <T> 值类型
 * @author zhangth
 * @since 1.0.0
 */
public interface DictionaryElement<T> {
    /**
     * 获取字典编码（返回枚举 name()）。
     *
     * @return 编码
     */
    String getCode();

    /**
     * 获取字典描述。
     *
     * @return 描述
     */
    String getMessage();

    /**
     * 获取字典值。
     *
     * @return 值
     */
    T getValue();
}
