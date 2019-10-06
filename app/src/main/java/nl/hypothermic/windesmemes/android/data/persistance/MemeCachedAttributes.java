package nl.hypothermic.windesmemes.android.data.persistance;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import nl.hypothermic.windesmemes.model.Vote;

@Entity
public class MemeCachedAttributes extends CachedAttributes {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "relativeUrl",
                typeAffinity = ColumnInfo.TEXT)
    @NonNull
    private String imageUrl;

    @ColumnInfo(name = "cachedImage",
                typeAffinity = ColumnInfo.BLOB)
    @NonNull
    private byte[] cachedImage;

    @ColumnInfo(name = "vote")
    @NonNull
    @TypeConverters({VoteTypeConverter.class})
    private Vote vote;

    @Deprecated
    @Ignore
    public MemeCachedAttributes() {

    }

    @Deprecated
    @Ignore
    public MemeCachedAttributes(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Deprecated
    @Ignore
    public MemeCachedAttributes(@NonNull String imageUrl, @NonNull byte[] cachedImage) {
        this.imageUrl = imageUrl;
        this.cachedImage = cachedImage;
    }

    public MemeCachedAttributes(@NonNull String imageUrl, @NonNull byte[] cachedImage, @NonNull Vote vote) {
        this.imageUrl = imageUrl;
        this.cachedImage = cachedImage;
        this.vote = vote;
    }

    public void setImageUrl(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCachedImage(@NonNull byte[] cachedImage) {
        this.cachedImage = cachedImage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public byte[] getCachedImage() {
        return cachedImage;
    }

    @NonNull
    public Vote getVote() {
        return vote;
    }

    public void setVote(@NonNull Vote vote) {
        this.vote = vote;
    }
}
