package nl.hypothermic.windesmemes.model;

import com.google.gson.annotations.SerializedName;

public enum MemeMode {

    @SerializedName("fresh")
    FRESH("fresh"),

    @SerializedName("hot")
    HOT("hot"),

    @SerializedName("trending")
    TRENDING("trending"),

    @SerializedName("best")
    BEST("best"),

    ;

    public static final MemeMode DEFAULT_MODE = FRESH;

    public static MemeMode fromSerialized(String serialized) {
        for (MemeMode mode : values()) {
            if (mode.modeStr.equals(serialized)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Mode not recognized");
    }

    private final String modeStr;

    private MemeMode(String modeStr) {
        this.modeStr = modeStr;
    }

    public String getAsString() {
        return modeStr;
    }
}
