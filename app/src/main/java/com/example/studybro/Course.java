package com.example.studybro;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "courses")
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int id;   // 主键，自动生成

    private String name;
    private String teacher;
    private String room;
    private int dayOfWeek; // 1-6 对应周一到周六
    private int startTime; // 如 9 表示 09:00
    private int endTime;   // 如 12 表示 12:00

    @Ignore
    private String dayAbbr;

    // Room 必须要有无参构造函数
    public Course() {}

    // 手动创建课程对象时使用的构造函数
    @Ignore
    public Course(String name, String teacher, String room,
                  int dayOfWeek, int startTime, int endTime) {
        this.name = name;
        this.teacher = teacher;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public int getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getStartTime() { return startTime; }
    public void setStartTime(int startTime) { this.startTime = startTime; }

    public int getEndTime() { return endTime; }
    public void setEndTime(int endTime) { this.endTime = endTime; }

    // dayAbbr 动态生成，不依赖数据库存储
    public String getDayAbbr() {
        String[] abbrs = {"M", "T", "W", "R", "F", "S"};
        return abbrs[dayOfWeek - 1];
    }

    public void setDayAbbr(String dayAbbr) {
        this.dayAbbr = dayAbbr;
    }

    public int getDuration() { return endTime - startTime; }
}
