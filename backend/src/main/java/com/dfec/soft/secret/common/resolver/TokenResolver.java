package com.dfec.soft.secret.common.resolver;

/**
 * Token 解析器接口，从 Token 中提取指定字段值。
 *
 * <p>不同 Token 格式（JWT、UUID、自定义）只需实现此接口。
 *
 * @author zhangth
 * @since 1.0.0
 */
@FunctionalInterface
public interface TokenResolver {

    /**
     * 从 Token 中解析指定字段的值。
     *
     * @param token     Token 字符串
     * @param fieldName 要提取的字段名
     * @return 字段值，未找到返回 null
     */
    Object resolve(String token, String fieldName);
}
