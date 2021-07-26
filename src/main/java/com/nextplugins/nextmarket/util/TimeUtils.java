package com.nextplugins.nextmarket.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm");

    public static String format(long value) {
        if (value <= 0) return "0 segundos";

        long days = TimeUnit.MILLISECONDS.toDays(value);
        long hours = TimeUnit.MILLISECONDS.toHours(value) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(value) - (TimeUnit.MILLISECONDS.toHours(value) * 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(value) - (TimeUnit.MILLISECONDS.toMinutes(value) * 60);

        long[] times = {days, hours, minutes, second};
        String[] names = {"d", "h", "m", "s"};

        List<String> values = new ArrayList<>();
        for (int index = 0; index < times.length; index++) {
            long time = times[index];
            if (time > 0) {
                String name = text(times[index], names[index]);
                values.add(name);
            }
        }

        if (values.size() == 1) {
            return values.get(0);
        }

        return String.join(" ", values);
    }

    public static String simpleFormat(long time) {
        final String date = dateFormat.format(new Date(time));

        final String day = date.split(" ")[0];
        final String hour = date.split(" ")[1];

        return String.format("%s às %s", day, hour);
    }

    public static String text(long quantity, String message) {
        return quantity + message;
    }

}
