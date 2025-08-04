package com.woochang.highticket.global.util;

import com.woochang.highticket.global.common.EnumValue;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumUtilsTest {
    enum TestGrade implements EnumValue {
        VIP("VIP"), R("R");

        private final String value;

        TestGrade(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    @Nested
    @DisplayName("fromValue 메서드는")
    class FromValue {

        @Test
        @DisplayName("올바른 문자열이 주어지면 Enum으로 반환한다")
        void success() {
            // when
            TestGrade result = EnumUtils.fromValue(TestGrade.class, "VIP");

            // then
            assertThat(result).isEqualTo(TestGrade.VIP);
        }

        @Test
        @DisplayName("존재하지 않는 문자열이 주어지면 예외가 발생한다")
        public void invalidValue_throwsException() {
            // when & then
            assertThatThrownBy(() -> EnumUtils.fromValue(TestGrade.class, "INVALID"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ENUM_VALUE.getMessage());
        }

        @Test
        @DisplayName("null이 주어지면 예외가 발생한다")
        public void nullValue_throwsException() {
            // when & then
            assertThatThrownBy(() -> EnumUtils.fromValue(TestGrade.class, null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ENUM_VALUE.getMessage());
        }
    }
}