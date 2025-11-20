package com.example.studybro;

public class Task {
    private String name;
    private String description;
    private int completionCount;
    private int pickCount; // 新增：记录被抽取的次数
    private boolean isCompleted;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.completionCount = 0;
        this.pickCount = 0; // 初始化抽取次数为0
        this.isCompleted = false;
    }

    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public void setCompletionCount(int completionCount) {
        this.completionCount = completionCount;
    }

    // 新增：抽取次数相关方法
    public int getPickCount() {
        return pickCount;
    }

    public void setPickCount(int pickCount) {
        this.pickCount = pickCount;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void markCompleted() {
        this.completionCount++;
        this.isCompleted = true;
    }

    public void resetCompletion() {
        this.isCompleted = false;
    }

    // 新增：增加抽取次数
    public void increasePickCount() {
        this.pickCount++;
    }
}