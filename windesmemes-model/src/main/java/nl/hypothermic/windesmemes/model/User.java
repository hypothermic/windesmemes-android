package nl.hypothermic.windesmemes.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class User {

    @SerializedName("user_id")
    public long userId;

    @SerializedName("username")
    public String username;

    @SerializedName("user_level")
    public int userLevel;

    @SerializedName("avatar_id")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                userLevel == user.userLevel &&
                createdMemes == user.createdMemes &&
                totalKarma == user.totalKarma &&
                owner == user.owner &&
                Objects.equals(username, user.username) &&
                Objects.equals(avatar_id, user.avatar_id) &&
                Objects.equals(biography, user.biography) &&
                Objects.equals(member_since, user.member_since) &&
                Objects.equals(flair, user.flair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, userLevel, avatar_id, biography, member_since, flair, createdMemes, totalKarma, owner);
    }
}
