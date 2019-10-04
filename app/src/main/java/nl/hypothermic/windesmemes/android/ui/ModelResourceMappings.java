package nl.hypothermic.windesmemes.android.ui;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import nl.hypothermic.windesmemes.android.R;
import nl.hypothermic.windesmemes.model.MemeMode;

public class ModelResourceMappings {

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

    public static @IdRes int getNavigationItem(MemeMode mode) {
        switch (mode) {
            case FRESH:
                return R.id.nav_mode_fresh;
            case HOT:
                return R.id.nav_mode_hot;
            case TRENDING:
                return R.id.nav_mode_trending;
            case BEST:
                return R.id.nav_mode_best;
        }
        throw new IllegalArgumentException("No mode matching " + mode.name());
    }

    private ModelResourceMappings() {
        throw new AssertionError("Not instantiable");
    }
}
