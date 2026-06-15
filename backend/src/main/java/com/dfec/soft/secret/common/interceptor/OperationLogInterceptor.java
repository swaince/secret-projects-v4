package com.dfec.soft.secret.common.interceptor;

import com.dfec.soft.secret.common.resolver.TokenResolver;
import com.dfec.soft.secret.common.resolver.UuidTokenResolver;
import com.dfec.soft.secret.system.entity.SysOperationLog;
import com.dfec.soft.secret.system.service.AsyncLogService;
import com.dfec.soft.secret.common.service.UidService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 操作日志拦截器，为每个 Controller 请求记录操作日志。
 *
 * <p>在 {@code preHandle} 中记录开始时间，在 {@code afterCompletion} 中收集数据并异步保存。
 * 与 {@link com.dfec.soft.secret.common.filter.AccessLogFilter} 配合使用，共享 {@code requestId}。
 *
 * @author zhangth
 * @since 1.0.0
 */
@Component
public class OperationLogInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationLogInterceptor.class);

    private static final String START_TIME_KEY = "logStartTime";

    private static final String EXCEPTION_ATTRIBUTE = "org.springframework.web.servlet.DispatcherServlet.EXCEPTION";

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final AsyncLogService asyncLogService;
    private final UidService uidService;
    private final TokenResolver tokenResolver;

    public OperationLogInterceptor(AsyncLogService asyncLogService, UidService uidService) {
        this.asyncLogService = asyncLogService;
        this.uidService = uidService;
        this.tokenResolver = new UuidTokenResolver();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_KEY, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_KEY);
        if (startTime == null) {
            return;
        }
        long duration = System.currentTimeMillis() - startTime;

        String controllerClass = null;
        String controllerMethod = null;
        if (handler instanceof HandlerMethod handlerMethod) {
            controllerClass = handlerMethod.getBeanType().getName();
            controllerMethod = handlerMethod.getMethod().getName();
        }

        try {
            SysOperationLog log = buildOperationLog(request, response, duration, controllerClass, controllerMethod);
            asyncLogService.saveOperationLog(log);
        } catch (Exception e) {
            LOGGER.error("记录操作日志失败", e);
        }
    }

    private SysOperationLog buildOperationLog(HttpServletRequest request, HttpServletResponse response,
            long duration, String controllerClass, String controllerMethod) {
        SysOperationLog log = new SysOperationLog();
        log.setLogId(uidService.next());
        log.setRequestId((String) request.getAttribute("requestId"));
        log.setRequestUrl(request.getRequestURI());
        log.setRequestMethod(request.getMethod());
        log.setRequestHeaders(buildHeadersJson(request));
        log.setRequestParams(buildParams(request));
        log.setRequestBody(null);
        log.setResponseStatus(response.getStatus());
        log.setDurationMs(duration);
        log.setClientIp(getClientIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        resolveUserInfo(request, log);
        log.setExceptionStack(getExceptionStack(request));
        log.setControllerClass(controllerClass);
        log.setControllerMethod(controllerMethod);
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

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void resolveUserInfo(HttpServletRequest request, SysOperationLog log) {
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
        Object ex = request.getAttribute(EXCEPTION_ATTRIBUTE);
        if (ex instanceof Throwable t) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            return sw.toString();
        }
        return null;
    }
}
