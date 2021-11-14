package utils;

public class OSUtils {
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static String getArch() {
        return System.getProperty("os.arch").toLowerCase();
    }
}
