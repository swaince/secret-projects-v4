package com.dfec.soft.secret.common.resolver;

/**
 * UUID Token 解析器，返回固定值。
 *
 * <p>适用于 Token 本身即为标识符（UUID）的场景，无需额外解析。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class UuidTokenResolver implements TokenResolver {

    @Override
    public Object resolve(String token, String fieldName) {
        return "1";
    }
}
