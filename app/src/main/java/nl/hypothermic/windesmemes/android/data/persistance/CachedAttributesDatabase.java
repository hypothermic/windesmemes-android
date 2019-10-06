package nl.hypothermic.windesmemes.android.data.persistance;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import nl.hypothermic.windesmemes.android.LogWrapper;

@Database(entities = { MemeCachedAttributes.class }, version = 5, exportSchema = false)
public abstract class CachedAttributesDatabase extends RoomDatabase {

    private static final String DATABASE_PATH = "cache.db";
    private static final long SIZE_LIMIT = 64L * 1000 * 1000; // = 64MB, TODO let user specify

    public static final Executor IO_THREAD = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "WM Database Input/Output Thread");
        }
    });

    private static CachedAttributesDatabase INSTANCE;
    private static final Object LOCK = new Object();

    public static CachedAttributesDatabase getInstance(final Context context) {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, CachedAttributesDatabase.class, DATABASE_PATH)
                                   .fallbackToDestructiveMigration()
                                   .build();

                IO_THREAD.execute(new Runnable() {
                    @Override
                    public void run() {
                        File database = context.getDatabasePath(DATABASE_PATH);
                        long size = database.length();
                        if (database.exists() && size > SIZE_LIMIT) {
                            LogWrapper.error(INSTANCE, "Clearing all tables because database was over size (%d bytes)", size);
                            INSTANCE.clearAllTables();
                        }
                    }
                });
            }
            return INSTANCE;
        }
    }

    public abstract MemeCachedAttributesDao getMemeCachedAttributesDao();

}
