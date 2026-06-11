package com.dfec.soft.secret.common.exception;

import com.dfec.soft.secret.common.constants.BizCode;

/**
 * 对外异常（消息可直接返回给调用方）。
 *
 * @author zhangth
 * @since 1.0.0
 */
public class OuterException extends BusinessException {
    public OuterException(BizCode bizCode, String message) { super(bizCode, message); }
    public OuterException(BizCode bizCode) { super(bizCode); }
}
