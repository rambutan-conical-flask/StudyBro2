package com.example.studybro;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class LedgerRepository {
    private LedgerDao ledgerDao;
    private LiveData<List<Ledger>> allLedgers;

    public LedgerRepository(Application application) {
        LedgerDatabase db = LedgerDatabase.getDatabase(application);
        ledgerDao = db.ledgerDao();
        allLedgers = ledgerDao.getAllLedgers();
        // 移除 monthlySummary，因为我们改用手动分组了
    }

    public LiveData<List<Ledger>> getAllLedgers() {
        return allLedgers;
    }

    public LiveData<List<Ledger>> getLedgersByYearMonth(int year, int month) {
        return ledgerDao.getLedgersByYearMonth(year, month);
    }

    public List<Ledger> getLedgersForMonth(int year, int month) {
        return ledgerDao.getLedgersForMonth(year, month);
    }

    public void insert(Ledger ledger) {
        LedgerDatabase.databaseWriteExecutor.execute(() -> {
            ledgerDao.insert(ledger);
        });
    }

    public void deleteById(long id) {
        LedgerDatabase.databaseWriteExecutor.execute(() -> {
            ledgerDao.deleteById(id);
        });
    }
}