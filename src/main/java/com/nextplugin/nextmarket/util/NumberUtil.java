package com.nextplugin.nextmarket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtil {

    private final static DecimalFormat numberFormat = new DecimalFormat("#,###.#");

    public static Double getDouble(String str) {
        return parseNumber(str, Double::parseDouble);
    }

    public static String formatNumber(Number number) {
        return numberFormat.format(number);
    }

    public static boolean isNaN(String str) {
        return !str.equalsIgnoreCase("nan");
    }

    private static <T extends Number> T parseNumber(String str, NumberConsumer<T> consumer) {
        if (!isNaN(str)) return null;

        try {
            return consumer.accept(str);
        } catch (Exception e) {
            return null;
        }
    }

    @FunctionalInterface
    private interface NumberConsumer<T> {

        T accept(String str);

    }

}
