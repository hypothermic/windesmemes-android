package nl.hypothermic.windesmemes.android;

import android.util.Log;

public class LogWrapper {

    private static final String LOG_TAG = "Windesmemes";

    public static void error(Object caller, String format, Object... data) {
        log(Log.ERROR, caller, format, data);
    }

    public static void warn(Object caller, String format, Object... data) {
        log(Log.WARN, caller, format, data);
    }

    public static void info(Object caller, String format, Object... data) {
        log(Log.INFO, caller, format, data);
    }

    private static void log(int priority, Object caller, String format, Object... data) {
        Log.println(priority, LOG_TAG,caller.getClass().getSimpleName() + String.format(format, data));
    }
}
