package com.dfec.soft.secret.common.config;

import com.dfec.soft.secret.common.constants.BizCode;
import com.dfec.soft.secret.common.dto.common.R;
import com.dfec.soft.secret.common.exception.BusinessException;
import com.dfec.soft.secret.common.exception.InnerException;
import com.dfec.soft.secret.common.exception.OuterException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 *
 * @author zhangth
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));
        return R.err(BizCode.BAD_REQUEST.getValue(), message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return R.err(BizCode.BAD_REQUEST.getValue(), "请求体格式错误");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return R.err(BizCode.BAD_REQUEST.getValue(), "缺少必要参数: " + e.getParameterName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return R.err(BizCode.BAD_REQUEST.getValue(), "不支持的请求方法: " + e.getMethod());
    }

    @ExceptionHandler(OuterException.class)
    public R<Void> handleOuter(OuterException e) {
        return R.err(e.getBizCode().getValue(), e.getMessage());
    }

    @ExceptionHandler(InnerException.class)
    public R<Void> handleInner(InnerException e) {
        LOGGER.error("inner exception: {}", e.getMessage(), e);
        return R.err(BizCode.INTERNAL_ERROR.getValue(), BizCode.INTERNAL_ERROR.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusiness(BusinessException e) {
        LOGGER.error("business exception: {}", e.getMessage(), e);
        return R.err(e.getBizCode().getValue(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntime(RuntimeException e) {
        LOGGER.error("runtime exception", e);
        return R.err(BizCode.INTERNAL_ERROR.getValue(), BizCode.INTERNAL_ERROR.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        LOGGER.error("exception", e);
        return R.err(BizCode.INTERNAL_ERROR.getValue(), BizCode.INTERNAL_ERROR.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public R<Void> handleThrowable(Throwable e) {
        LOGGER.error("throwable", e);
        return R.err(BizCode.INTERNAL_ERROR.getValue(), BizCode.INTERNAL_ERROR.getMessage());
    }
}
