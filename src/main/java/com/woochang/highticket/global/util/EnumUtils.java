package com.woochang.highticket.global.util;

import com.woochang.highticket.global.common.EnumValue;
import com.woochang.highticket.global.exception.BusinessException;
import com.woochang.highticket.global.exception.ErrorCode;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EnumUtils {

    private static final Map<Class<?>, Map<String, Enum<?>>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & EnumValue> T fromValue(Class<T> enumClass, String value) {
        Map<String, Enum<?>> enumMap = cache.computeIfAbsent(enumClass, clazz ->
                Arrays.stream(clazz.getEnumConstants())
                        .collect(Collectors.toMap(
                                e -> ((EnumValue) e).getValue(),
                                e -> (Enum<?>) e
                        ))
        );

        Enum<?> result = enumMap.get(value);
        if (result == null) {
            throw new BusinessException(ErrorCode.INVALID_ENUM_VALUE);
        }

        return (T) result;
    }
}