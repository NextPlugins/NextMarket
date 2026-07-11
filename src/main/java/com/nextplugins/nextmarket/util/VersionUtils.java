package com.nextplugins.nextmarket.util;

import com.nextplugins.nextmarket.compat.ServerVersion;

/**
 * @deprecated Use {@link ServerVersion} instead.
 */
@Deprecated
public final class VersionUtils {

    public static boolean isLegacy() {
        return ServerVersion.isLegacy();
    }

}
