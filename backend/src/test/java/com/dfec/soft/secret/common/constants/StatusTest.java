package com.dfec.soft.secret.common.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 状态枚举测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
class StatusTest {

    @Test
    void shouldReturnCorrectValue() {
        assertThat(Status.ENABLED.getValue()).isEqualTo(1);
        assertThat(Status.DISABLED.getValue()).isEqualTo(0);
    }

    @Test
    void shouldReturnCorrectMessage() {
        assertThat(Status.ENABLED.getMessage()).isEqualTo("启用");
        assertThat(Status.DISABLED.getMessage()).isEqualTo("禁用");
    }
}
