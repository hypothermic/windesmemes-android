package nl.hypothermic.windesmemes.android.ui;

import androidx.annotation.StyleRes;

import nl.hypothermic.windesmemes.android.R;

public enum ActivityTheme {

    //LIGHT(0, R.style.AppTheme_LIGHT),
    LIGHT(0, R.style.AppTheme),
    DARK (1, R.style.AppTheme_DARK),

    ;

    public static ActivityTheme fromIndex(int index) {
        return values()[index];
    }

    private final int index, styleId;

    private ActivityTheme(int index, @StyleRes int styleId) {
        this.index = index;
        this.styleId = styleId;
    }

    public int getIndex() {
        return index;
    }

    public int getStyleId() {
        return styleId;
    }
}
