package com.example.studybro;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LedgerViewModel extends AndroidViewModel {
    private LedgerRepository repository;
    private LiveData<List<Ledger>> allLedgers;

    public LedgerViewModel(@NonNull Application application) {
        super(application);
        repository = new LedgerRepository(application);
        allLedgers = repository.getAllLedgers();
        // 移除 monthlySummary，因为我们改用手动分组了
    }

    public LiveData<List<Ledger>> getAllLedgers() {
        return allLedgers;
    }

    public LiveData<List<Ledger>> getLedgersByYearMonth(int year, int month) {
        return repository.getLedgersByYearMonth(year, month);
    }

    public List<Ledger> getLedgersForMonth(int year, int month) {
        return repository.getLedgersForMonth(year, month);
    }

    public void addLedger(String type, String name, double amount, int year, int month) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Ledger ledger = new Ledger(name, amount, year, month, date, type);
        repository.insert(ledger);
    }

    public void deleteLedger(long id) {
        repository.deleteById(id);
    }
}