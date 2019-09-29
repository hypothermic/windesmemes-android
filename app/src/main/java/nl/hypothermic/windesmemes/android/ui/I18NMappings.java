package nl.hypothermic.windesmemes.android.ui;

import androidx.annotation.StringRes;

import nl.hypothermic.windesmemes.android.R;
import nl.hypothermic.windesmemes.model.MemeMode;

public class I18NMappings {

    public static @StringRes int getModeResource(MemeMode mode) {
        switch (mode) {
            case FRESH:
                return R.string.mode_fresh;
            case HOT:
                return R.string.mode_hot;
            case TRENDING:
                return R.string.mode_trending;
            case BEST:
                return R.string.mode_best;
        }
        throw new IllegalArgumentException("No mode matching " + mode.name());
    }

    private I18NMappings() {

    }
}
