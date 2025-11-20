package com.example.studybro;

import androidx.room.Ignore;

public class LedgerMonth {
    public int year;
    public int month;
    public double incomeTotal;
    public double expenseTotal;

    @Ignore
    public boolean isExpanded = false;

    public LedgerMonth() {
    }

    @Ignore
    public LedgerMonth(int year, int month, double incomeTotal, double expenseTotal) {
        this.year = year;
        this.month = month;
        this.incomeTotal = incomeTotal;
        this.expenseTotal = expenseTotal;
    }
}