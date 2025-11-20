package com.example.studybro;

public class MonthRecord {
    private int year;
    private int month;
    private double expenditure; // 支出总额
    private double income;      // 收入总额
    private boolean isExpanded;

    public MonthRecord(int year, int month, double expenditure, double income, boolean isExpanded) {
        this.year = year;
        this.month = month;
        this.expenditure = expenditure;
        this.income = income;
        this.isExpanded = isExpanded;
    }

    public int getYear() { return year; }
    public int getMonth() { return month; }
    public double getExpenditure() { return expenditure; }
    public double getIncome() { return income; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
