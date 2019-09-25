package nl.hypothermic.windesmemes.android.data.persistance;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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

    @Ignore
    public MemeCachedAttributes() {

    }

    @Ignore
    public MemeCachedAttributes(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MemeCachedAttributes(@NonNull String imageUrl, @NonNull byte[] cachedImage) {
        this.imageUrl = imageUrl;
        this.cachedImage = cachedImage;
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
}
