package nl.hypothermic.windesmemes.model;

import com.google.gson.annotations.SerializedName;

public class Meme {

    @SerializedName("id")
    public long id;

    @SerializedName("username")
    public String username;

    @SerializedName("user_id")
    public long userId;

    @SerializedName("karma")
    public int userKarma; // zal nooit een 'long' worden

    @SerializedName("vote")
    public int vote;

    @SerializedName("flair")
    public String flair; // TODO data type????

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("title")
    public String title;

    @SerializedName("date")
    public String date; // TODO format

}
