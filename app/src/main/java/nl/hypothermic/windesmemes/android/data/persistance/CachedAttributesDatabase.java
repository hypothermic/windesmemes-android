package nl.hypothermic.windesmemes.android.data.persistance;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Database(entities = { MemeCachedAttributes.class }, version = 4, exportSchema = false)
public abstract class CachedAttributesDatabase extends RoomDatabase {

    public static final Executor THREAD = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "WM Database Thread");
        }
    });

    private static CachedAttributesDatabase INSTANCE;
    private static final Object LOCK = new Object();

    public static CachedAttributesDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, CachedAttributesDatabase.class, "cache.db").build();
            }
            return INSTANCE;
        }
    }

    public abstract MemeCachedAttributesDao getMemeCachedAttributesDao();

}
