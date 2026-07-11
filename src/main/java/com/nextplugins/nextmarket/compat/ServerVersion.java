package com.nextplugins.nextmarket.compat;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects the running Minecraft server version without relying on NMS package names
 * (which Paper no longer exposes on modern builds).
 */
public final class ServerVersion {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?");

    private static final int MAJOR;
    private static final int MINOR;
    private static final int PATCH;
    private static final boolean LEGACY;

    static {
        int major = 1;
        int minor = 0;
        int patch = 0;

        String raw = Bukkit.getBukkitVersion();
        if (raw == null || raw.isEmpty()) {
            raw = Bukkit.getVersion();
        }

        // Prefer the part before '-' (e.g. "1.20.4-R0.1-SNAPSHOT" or "26.2-R0.1-SNAPSHOT")
        String candidate = raw;
        int dash = raw.indexOf('-');
        if (dash > 0) {
            candidate = raw.substring(0, dash);
        }

        Matcher matcher = VERSION_PATTERN.matcher(candidate);
        if (matcher.find()) {
            major = parse(matcher.group(1), 1);
            minor = parse(matcher.group(2), 0);
            patch = parse(matcher.group(3), 0);
        } else {
            // Fallback: scan full version string
            matcher = VERSION_PATTERN.matcher(raw);
            if (matcher.find()) {
                major = parse(matcher.group(1), 1);
                minor = parse(matcher.group(2), 0);
                patch = parse(matcher.group(3), 0);
            }
        }

        MAJOR = major;
        MINOR = minor;
        PATCH = patch;
        // Pre-1.13 uses durability / data values. Year-based versions (26.x) are never legacy.
        LEGACY = MAJOR == 1 && MINOR < 13;
    }

    private ServerVersion() {
    }

    private static int parse(String value, int fallback) {
        if (value == null) return fallback;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static int major() {
        return MAJOR;
    }

    public static int minor() {
        return MINOR;
    }

    public static int patch() {
        return PATCH;
    }

    /** True for Minecraft 1.8 – 1.12.x (material data / durability era). */
    public static boolean isLegacy() {
        return LEGACY;
    }

    public static boolean isAtLeast(int major, int minor) {
        if (MAJOR != major) {
            return MAJOR > major;
        }
        return MINOR >= minor;
    }

    public static String asString() {
        return MAJOR + "." + MINOR + "." + PATCH;
    }

}
