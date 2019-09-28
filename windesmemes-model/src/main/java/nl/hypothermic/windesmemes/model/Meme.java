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
    public String karma;

    @SerializedName("vote")
    public String vote;

    @SerializedName("flair")
    public String flair; // TODO data type????

    @SerializedName("image")
    public String imageUrl;

    @SerializedName("title")
    public String title;

    @SerializedName("date")
    public String date; // TODO format

    @Override
    public String toString() {
        return "Meme [" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", userId=" + userId +
                ", karma=" + karma +
                ", vote=" + vote +
                ", flair='" + flair + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                "]";
    }

    public int parseKarma() {
        return Integer.valueOf(karma);
    }

    public int parseVote() {
        return Integer.valueOf(vote);
    }
}
