package nl.hypothermic.windesmemes.android.util;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

public class LocaleCompat {

    public static Locale getDefaultLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    private LocaleCompat() {
        throw new AssertionError("Not instantiable");
    }
}
