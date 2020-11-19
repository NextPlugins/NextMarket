package com.nextplugin.nextmarket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtil {

    private final static DecimalFormat numberFormat = new DecimalFormat("#,###.#");

    private final static List<String> chars = Arrays.asList("", "K", "M", "B", "T", "Q", "QQ", "S", "SS", "O", "N", "D",
            "UN", "DD", "TR", "QT", "QN", "SD", "SPD", "OD", "ND", "VG", "UVG", "DVG", "TVG", "QTV");


    public static Double getDouble(String str) {
        return parseNumber(str, Double::parseDouble);
    }

    public static String formatNumber(Number number) {
        return numberFormat.format(number);
    }

    public static String letterFormat(double number) {
        int index = 0;
        while (number / 1000.0D >= 1.0D) {
            number /= 1000.0D;
            index++;
        }

        String character = index < chars.size() ? chars.get(index) : "";
        return numberFormat.format(number) + character;
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
