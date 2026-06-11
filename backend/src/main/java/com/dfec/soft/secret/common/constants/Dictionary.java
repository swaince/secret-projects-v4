package com.dfec.soft.secret.common.constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典注解，声明字典元信息。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dictionary {
    /**
     * 字典中文名称，如 "状态"。
     *
     * @return 名称
     */
    String name();

    /**
     * 字典编码，如 "status"。
     *
     * @return 编码
     */
    String code();
}
