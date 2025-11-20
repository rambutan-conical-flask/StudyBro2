package com.example.studybro;

import java.util.ArrayList;
import java.util.List;

public class LedgerGroup {
    public int year;
    public int month;
    public double totalIncome = 0;
    public double totalExpense = 0;
    public boolean expanded = false; // 默认折叠
    public List<Ledger> ledgerList = new ArrayList<>();

    public LedgerGroup(int year, int month) {
        this.year = year;
        this.month = month;
    }
}
