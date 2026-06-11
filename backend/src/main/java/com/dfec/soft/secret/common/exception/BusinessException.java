package com.dfec.soft.secret.common.exception;

import com.dfec.soft.secret.common.constants.BizCode;

/**
 * 业务异常基类。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {
    private final BizCode bizCode;

    public BusinessException(BizCode bizCode, String message) { super(message); this.bizCode = bizCode; }
    public BusinessException(BizCode bizCode) { this(bizCode, bizCode.getMessage()); }
    public BizCode getBizCode() { return bizCode; }
}
