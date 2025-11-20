package com.example.studybro;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Ledger.class}, version = 2, exportSchema = false)
public abstract class LedgerDatabase extends RoomDatabase {
    private static volatile LedgerDatabase INSTANCE;
    public abstract LedgerDao ledgerDao();

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static LedgerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LedgerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    LedgerDatabase.class, "ledger_db")
                            .fallbackToDestructiveMigration() // 清空旧数据
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
