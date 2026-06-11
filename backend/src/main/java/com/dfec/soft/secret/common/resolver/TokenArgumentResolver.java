package com.dfec.soft.secret.common.resolver;

import com.dfec.soft.secret.common.annotation.TokenParam;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Token 参数解析器，从请求 Token 中解析指定字段值。
 *
 * <p>Token 格式由 {@link TokenResolver} 决定（默认 JWT）。
 * 用法与 {@code @RequestParam} 完全一致：
 * <pre>{@code
 * public R<DictDTO> create(@TokenParam("userId") String userId) { }
 * }</pre>
 *
 * @author zhangth
 * @since 1.0.0
 */
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenArgumentResolver.class);

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String PARAM_TOKEN = "token";

    private final TokenResolver tokenResolver;

    public TokenArgumentResolver(TokenResolver tokenResolver) {
        this.tokenResolver = tokenResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(TokenParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        String token = getToken(webRequest);
        if (token == null) {
            return null;
        }
        String fieldName = getFieldName(parameter);
        return tokenResolver.resolve(token, fieldName);
    }

    private String getToken(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }
        String authorization = request.getHeader(HEADER_AUTHORIZATION);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return request.getParameter(PARAM_TOKEN);
    }

    private String getFieldName(MethodParameter parameter) {
        TokenParam annotation = parameter.getParameterAnnotation(TokenParam.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return parameter.getParameterName();
    }
}
