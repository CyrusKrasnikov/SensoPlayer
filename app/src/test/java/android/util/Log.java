package android.util;

/**
 * https://medium.com/@gal_41749/android-unitests-and-log-class-9546b6480006
 */
public class Log {
    public static int d(String tag, String message) {
        System.out.println(tag + ": " + message);
        return 0;
    }
}