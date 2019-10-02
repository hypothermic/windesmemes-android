package nl.hypothermic.windesmemes.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    public long userId;

    @SerializedName("username")
    public String username;

    @SerializedName("user_level")
    public int userLevel;

    @SerializedName("avatar")
    public String avatar_id;

    @SerializedName("bio")
    public String biography;

    @SerializedName("member_since")
    public String member_since; // TODO date

    @SerializedName("flair")
    public String flair; // TODO flair

    @SerializedName("created_memes")
    public int createdMemes;

    @SerializedName("total_karma")
    public int totalKarma;

    @SerializedName("owner")
    public boolean owner;

}
