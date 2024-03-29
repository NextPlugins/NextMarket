package com.nextplugins.nextmarket.util;

import com.nextplugins.nextmarket.configuration.value.MessageValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+\\.?\\d*)(\\D+)");

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static String formatNumber(double value) {

        if (isInvalid(value)) return "0";

        int index = 0;

        double tmp;
        while ((tmp = value / 1000) >= 1) {
            value = tmp;
            ++index;
        }

        return DECIMAL_FORMAT.format(value) + MessageValue.get(MessageValue::currencyFormat).get(index);

    }

    public static double parse(String string) {
        try {

            val value = Double.parseDouble(string);
            return isInvalid(value) ? 0 : value;

        } catch (Exception ignored) {
        }

        Matcher matcher = PATTERN.matcher(string);
        if (!matcher.find()) return -1;

        double amount = Double.parseDouble(matcher.group(1));
        String suffix = matcher.group(2);

        int index = MessageValue.get(MessageValue::currencyFormat).indexOf(suffix.toUpperCase());

        val value = amount * Math.pow(1000, index);
        return isInvalid(value) ? 0 : value;

    }

    public static boolean isInvalid(double value) {
        return value < 0 || Double.isNaN(value) || Double.isInfinite(value);
    }
}
