package nl.hypothermic.windesmemes.model;

import com.google.gson.annotations.SerializedName;

public class ActionResult {

    @SerializedName("error")
    public String error;

    public ActionResult(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
