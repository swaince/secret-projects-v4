package com.dfec.soft.secret.common.service;

/**
 * UID 生成服务。
 *
 * @author zhangth
 * @since 1.0.0
 */
public interface UidService {

    /**
     * 生成下一个唯一 ID（UUID v7，时间有序）。
     *
     * @return 32 位字符串 ID
     */
    String next();
}
