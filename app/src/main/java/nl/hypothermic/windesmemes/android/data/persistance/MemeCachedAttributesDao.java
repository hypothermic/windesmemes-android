package nl.hypothermic.windesmemes.android.data.persistance;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.List;

import nl.hypothermic.windesmemes.model.Vote;

@Dao
public interface MemeCachedAttributesDao {

    @Query("SELECT COUNT(*) FROM MemeCachedAttributes WHERE relativeUrl = :imageUrl LIMIT 1")
    LiveData<Integer> exists(String imageUrl);

    @Query("SELECT * FROM MemeCachedAttributes")
    LiveData<List<MemeCachedAttributes>> getAll();

    @Query("SELECT * FROM MemeCachedAttributes WHERE relativeUrl = :imageUrl LIMIT 1")
    LiveData<MemeCachedAttributes> get(String imageUrl);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<MemeCachedAttributes> posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(MemeCachedAttributes post);

    @Update
    void update(MemeCachedAttributes post);

    @Query("UPDATE MemeCachedAttributes SET vote = :vote WHERE relativeUrl = :imageUrl")
    @TypeConverters({VoteTypeConverter.class})
    void updateVote(String imageUrl, Vote vote);

    @Delete
    void delete(MemeCachedAttributes post);

}
