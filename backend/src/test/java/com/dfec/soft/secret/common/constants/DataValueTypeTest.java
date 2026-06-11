package com.dfec.soft.secret.common.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 数据值类型枚举测试。
 *
 * @author zhangth
 * @since 1.0.0
 */
class DataValueTypeTest {

    @Test
    void shouldReturnCorrectValues() {
        assertThat(DataValueType.STRING.getValue()).isEqualTo("STRING");
        assertThat(DataValueType.NUMBER.getValue()).isEqualTo("NUMBER");
        assertThat(DataValueType.BOOLEAN.getValue()).isEqualTo("BOOLEAN");
        assertThat(DataValueType.OBJECT.getValue()).isEqualTo("OBJECT");
        assertThat(DataValueType.ARRAY.getValue()).isEqualTo("ARRAY");
    }
}
