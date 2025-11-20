package com.example.studybro;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LedgerDao {
    @Insert
    void insert(Ledger ledger);

    @Query("SELECT * FROM ledgers ORDER BY year DESC, month DESC, id DESC")
    LiveData<List<Ledger>> getAllLedgers();

    @Query("SELECT * FROM ledgers WHERE year = :year AND month = :month ORDER BY id DESC")
    LiveData<List<Ledger>> getLedgersByYearMonth(int year, int month);

    @Query("SELECT * FROM ledgers WHERE year = :year AND month = :month ORDER BY id DESC")
    List<Ledger> getLedgersForMonth(int year, int month);

    @Query("DELETE FROM ledgers WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM ledgers")
    void deleteAll();
}