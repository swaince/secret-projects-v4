package com.dfec.soft.secret.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Token 参数注解，从 Token 中解析指定字段并绑定到方法参数。
 *
 * <p>用法与 {@code @RequestParam} 完全一致：
 * <pre>{@code
 * // 从 Token 中提取 userId 并赋值
 * public R<DictDTO> create(@TokenParam("userId") String userId) { }
 *
 * // 省略 value 时，使用参数名匹配
 * public R<DictDTO> update(@TokenParam String userId) { }
 * }</pre>
 *
 * @author zhangth
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenParam {

    /**
     * Token 中要提取的字段名，默认为空时使用参数名。
     *
     * @return 字段名
     */
    String value() default "";
}
