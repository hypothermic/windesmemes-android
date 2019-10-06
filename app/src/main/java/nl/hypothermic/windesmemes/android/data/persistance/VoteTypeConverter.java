package nl.hypothermic.windesmemes.android.data.persistance;

import androidx.room.TypeConverter;

import nl.hypothermic.windesmemes.model.Vote;

public class VoteTypeConverter {

    @TypeConverter
    public static Vote toVote(int status) {
        return Vote.fromIndex(status);
    }

    @TypeConverter
    public static Integer toInteger(Vote vote) {
        return vote.getWeight();
    }
}
