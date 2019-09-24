package nl.hypothermic.windesmemes.android;

import android.util.Log;

public class LogWrapper {

    private static final String LOG_TAG = "WindesMemes";

    public static void error(Object caller, String format, Object... data) {
        Log.e(LOG_TAG, caller.getClass().getSimpleName() + String.format(format, data));
    }

    public static void warn(Object caller, String format, Object... data) {
        Log.w(LOG_TAG, caller.getClass().getSimpleName() + String.format(format, data));
    }
}
