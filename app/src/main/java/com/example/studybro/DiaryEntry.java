package com.example.studybro;

public class DiaryEntry {
    public long id;          // 用时间戳当唯一ID
    public int moodIndex;
    public String text;

    public DiaryEntry(long id, int moodIndex, String text) {
        this.id = id;
        this.moodIndex = moodIndex;
        this.text = text;
    }
}