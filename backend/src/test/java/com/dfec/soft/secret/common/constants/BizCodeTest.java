package com.dfec.soft.secret.common.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 业务编码枚举测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
class BizCodeTest {

    @Test
    void shouldReturnCorrectValue() {
        assertThat(BizCode.SUCCESS.getValue()).isEqualTo(200);
        assertThat(BizCode.BAD_REQUEST.getValue()).isEqualTo(400);
        assertThat(BizCode.NOT_FOUND.getValue()).isEqualTo(404);
        assertThat(BizCode.INTERNAL_ERROR.getValue()).isEqualTo(500);
    }

    @Test
    void shouldReturnCorrectCode() {
        assertThat(BizCode.SUCCESS.getCode()).isEqualTo("SUCCESS");
    }

    @Test
    void shouldBeDictionaryElement() {
        assertThat(BizCode.SUCCESS).isInstanceOf(DictionaryElement.class);
    }
}
