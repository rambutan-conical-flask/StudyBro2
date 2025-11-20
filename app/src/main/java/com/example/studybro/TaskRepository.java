package com.example.studybro;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private List<Task> tasks;
    private List<Task> recentlySelected;

    public TaskRepository() {
        tasks = new ArrayList<>();
        recentlySelected = new ArrayList<>();
    }

    // 添加任务
    public void addTask(String name, String description) {
        tasks.add(new Task(name, description));
    }

    // 删除任务
    public void removeTask(int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.remove(position);
        }
    }

    // 清空列表
    public void clearAllTasks() {
        tasks.clear();
        recentlySelected.clear();
    }

    // 获取所有任务
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    // 标记任务完成
    public void markTaskCompleted(int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.get(position).markCompleted();
        }
    }

    // 重置任务完成状态
    public void resetTaskCompletion(int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.get(position).resetCompletion();
        }
    }

    // 随机选择任务（包含防重复逻辑）- 修复：只在这里增加计数
    public Task pickRandomTask(boolean avoidRepeat) {
        if (tasks.isEmpty()) {
            return null;
        }

        List<Task> availableTasks;
        if (avoidRepeat && recentlySelected.size() >= 3) {
            availableTasks = new ArrayList<>();
            for (Task task : tasks) {
                if (!recentlySelected.contains(task)) {
                    availableTasks.add(task);
                }
            }
            if (availableTasks.isEmpty()) {
                availableTasks = new ArrayList<>(tasks);
            }
        } else {
            availableTasks = new ArrayList<>(tasks);
        }

        int randomIndex = (int) (Math.random() * availableTasks.size());
        Task selected = availableTasks.get(randomIndex);

        // 更新抽取次数 - 确保每次抽取只记录一次
        selected.increasePickCount();

        // 添加到最近选择列表
        if (avoidRepeat) {
            recentlySelected.add(selected);
            if (recentlySelected.size() > 3) {
                recentlySelected.remove(0);
            }
        }

        return selected;
    }
}