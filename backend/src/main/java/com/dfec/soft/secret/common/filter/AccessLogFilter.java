package com.dfec.soft.secret.common.filter;

import com.dfec.soft.secret.common.resolver.TokenResolver;
import com.dfec.soft.secret.common.resolver.UuidTokenResolver;
import com.dfec.soft.secret.system.entity.SysAccessLog;
import com.dfec.soft.secret.system.service.AsyncLogService;
import com.dfec.soft.secret.common.service.UidService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 访问日志过滤器，为每个 HTTP 请求记录访问日志。
 *
 * <p>使用 {@link ContentCachingRequestWrapper} 包装请求，在请求完成后写入日志。
 * 日志保存通过 {@link AsyncLogService} 异步执行，不阻塞请求线程。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    private static final int BODY_LIMIT = 4096;

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String ERROR_EXCEPTION = "javax.servlet.error.exception";

    private static final String EXCEPTION_ATTRIBUTE = "org.springframework.web.servlet.DispatcherServlet.EXCEPTION";

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final AsyncLogService asyncLogService;
    private final UidService uidService;
    private final TokenResolver tokenResolver;

    public AccessLogFilter(AsyncLogService asyncLogService, UidService uidService) {
        this.asyncLogService = asyncLogService;
        this.uidService = uidService;
        this.tokenResolver = new UuidTokenResolver();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString().replace("-", "");
        request.setAttribute("requestId", requestId);

        boolean isMultipart = request.getContentType() != null && request.getContentType().startsWith("multipart/");
        HttpServletRequest wrapper = isMultipart ? request : new ContentCachingRequestWrapper(request, -1);
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(isMultipart ? request : wrapper, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            try {
                SysAccessLog log = buildAccessLog(requestId, wrapper, response, duration, isMultipart);
                asyncLogService.saveAccessLog(log);
            } catch (Exception e) {
                LOGGER.error("记录访问日志失败", e);
            }
        }
    }

    private SysAccessLog buildAccessLog(String requestId, HttpServletRequest wrapper,
            HttpServletResponse response, long duration, boolean isMultipart) {
        SysAccessLog log = new SysAccessLog();
        log.setLogId(uidService.next());
        log.setRequestId(requestId);
        log.setRequestUrl(wrapper.getRequestURI());
        log.setRequestMethod(wrapper.getMethod());
        log.setRequestHeaders(buildHeadersJson(wrapper));
        log.setRequestParams(buildParams(wrapper));
        log.setRequestBody(isMultipart ? null : buildBody(wrapper));
        log.setResponseStatus(response.getStatus());
        log.setDurationMs(duration);
        log.setClientIp(getClientIp(wrapper));
        log.setUserAgent(wrapper.getHeader("User-Agent"));
        resolveUserInfo(wrapper, log);
        log.setExceptionStack(getExceptionStack(wrapper));
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }

    private String buildHeadersJson(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Collections.list(request.getHeaderNames()).forEach(name ->
                headers.put(name, request.getHeader(name)));
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(headers);
        } catch (Exception e) {
            return null;
        }
    }

    private String buildParams(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        if (paramMap.isEmpty()) {
            return null;
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(paramMap);
        } catch (Exception e) {
            return null;
        }
    }

    private String buildBody(HttpServletRequest request) {
        if (!(request instanceof ContentCachingRequestWrapper wrapper)) {
            return null;
        }
        byte[] content = wrapper.getContentAsByteArray();
        if (content.length == 0) {
            return null;
        }
        int len = Math.min(content.length, BODY_LIMIT);
        return new String(content, 0, len, StandardCharsets.UTF_8);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void resolveUserInfo(HttpServletRequest request, SysAccessLog log) {
        String token = extractToken(request);
        if (token != null) {
            Object uid = tokenResolver.resolve(token, "userId");
            Object uname = tokenResolver.resolve(token, "username");
            if (uid != null) {
                log.setUserId(uid.toString());
            }
            if (uname != null) {
                log.setUsername(uname.toString());
            }
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader(HEADER_AUTHORIZATION);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return request.getParameter("token");
    }

    private String getExceptionStack(HttpServletRequest request) {
        Object ex1 = request.getAttribute(ERROR_EXCEPTION);
        if (ex1 instanceof Throwable t) {
            return stackTraceToString(t);
        }
        Object ex2 = request.getAttribute(EXCEPTION_ATTRIBUTE);
        if (ex2 instanceof Throwable t) {
            return stackTraceToString(t);
        }
        return null;
    }

    private String stackTraceToString(Throwable t) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
