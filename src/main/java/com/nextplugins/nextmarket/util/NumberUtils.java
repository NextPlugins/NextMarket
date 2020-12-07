package com.nextplugins.nextmarket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.function.Function;

/**
 * @author Henry Fábio
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

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

    private static <T extends Number> T parseNumber(String str, Function<String, T> consumer) {
        if (!isNaN(str)) return null;

        try {
            return consumer.apply(str);
        } catch (Exception e) {
            return null;
        }
    }

}
