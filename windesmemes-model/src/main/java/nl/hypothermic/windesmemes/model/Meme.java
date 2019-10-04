package nl.hypothermic.windesmemes.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meme meme = (Meme) o;
        return id == meme.id &&
                userId == meme.userId &&
                Objects.equals(username, meme.username) &&
                Objects.equals(karma, meme.karma) &&
                Objects.equals(vote, meme.vote) &&
                Objects.equals(flair, meme.flair) &&
                Objects.equals(imageUrl, meme.imageUrl) &&
                Objects.equals(title, meme.title) &&
                Objects.equals(date, meme.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, userId, karma, vote, flair, imageUrl, title, date);
    }
}
