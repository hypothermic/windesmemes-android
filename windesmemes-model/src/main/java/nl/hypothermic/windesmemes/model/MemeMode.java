package nl.hypothermic.windesmemes.model;

import com.google.gson.annotations.SerializedName;

public enum MemeMode {

    @SerializedName("fresh")
    FRESH("fresh")

    ;

    private final String modeStr;

    private MemeMode(String modeStr) {
        this.modeStr = modeStr;
    }

    public String getAsString() {
        return modeStr;
    }
}
