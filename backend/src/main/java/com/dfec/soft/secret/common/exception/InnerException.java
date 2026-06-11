package com.dfec.soft.secret.common.exception;

import com.dfec.soft.secret.common.constants.BizCode;

/**
 * 内部异常（含敏感信息，不直接返回给调用方）。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class InnerException extends BusinessException {
    public InnerException(BizCode bizCode, String message) { super(bizCode, message); }
    public InnerException(BizCode bizCode) { super(bizCode); }
}
