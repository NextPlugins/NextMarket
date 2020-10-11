package com.nextplugin.nextmarket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtil {

    private final static Random random = new Random();
    private final static DecimalFormat numberFormat = new DecimalFormat("#,###.#");

    public static Integer getInt(String str) {
        return parseNumber(str, Integer::parseInt);
    }

    public static Long getLong(String str) {
        return parseNumber(str, Long::parseLong);
    }

    public static Float getFloat(String str) {
        return parseNumber(str, Float::parseFloat);
    }

    public static Double getDouble(String str) {
        return parseNumber(str, Double::parseDouble);
    }

    public static Byte getByte(String str) {
        return parseNumber(str, Byte::parseByte);
    }

    public static Short getShort(String str) {
        return parseNumber(str, Short::parseShort);
    }

    public static int getRandomInt(Random random, int max) {
        return random.nextInt(max);
    }

    public static int getRandomInt(int max) {
        return getRandomInt(random, max);
    }

    public static boolean checkChance(int chance) {
        return getRandomInt(100) <= chance;
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
