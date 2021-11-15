package utils;

import java.util.Locale;

public class OSUtils {
    public enum OS {
        WINDOWS("windows"),
        MACOS("darwin"),
        LINUX("linux"),
        INVALID("invalid");

        private final String value;

        OS(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static OS getOSType() {
        OS detected;
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ROOT);
        if (os.contains("mac") || os.contains("darwin")) {
            detected = OS.MACOS;
        } else if (os.contains("win")) {
            detected = OS.WINDOWS;
        } else if (os.contains("nux")) {
            detected = OS.LINUX;
        } else {
            detected = OS.INVALID;
        }
        return detected;
    }

    public static String getOSName() {
        return getOSType().getValue();
    }

    public static String getArch() {
        return System.getProperty("os.arch").toLowerCase();
    }
}
