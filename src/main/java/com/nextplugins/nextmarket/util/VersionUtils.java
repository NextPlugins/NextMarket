package com.nextplugins.nextmarket.util;

public final class VersionUtils {

    public static boolean isLegacy() {
        int version = ReflectionUtils.getVersionNumber();

        return version < 113;
    }

}
