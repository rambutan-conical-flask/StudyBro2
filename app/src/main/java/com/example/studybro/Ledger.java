package com.example.studybro;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ledgers")
public class Ledger {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;      // 项目名称
    private double amount;    // 金额
    private int year;         // 年份
    private int month;        // 月份（数字）
    private String date;      // 添加日期
    private String type;      // "income" 或 "expense"

    public Ledger(String name, double amount, int year, int month, String date, String type) {
        this.name = name;
        this.amount = amount;
        this.year = year;
        this.month = month;
        this.date = date;
        this.type = type;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public String getDate() { return date; }
    public String getType() { return type; }

    public void setName(String name) { this.name = name; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setYear(int year) { this.year = year; }
    public void setMonth(int month) { this.month = month; }
    public void setDate(String date) { this.date = date; }
    public void setType(String type) { this.type = type; }
}
