package com.example.studybro;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class TaskBoxViewModel extends ViewModel {
    private TaskRepository repository;
    private MutableLiveData<List<Task>> taskList;
    private MutableLiveData<Task> selectedTask;
    private MutableLiveData<Boolean> isRolling;

    public TaskBoxViewModel() {
        repository = new TaskRepository();
        taskList = new MutableLiveData<>();
        selectedTask = new MutableLiveData<>();
        isRolling = new MutableLiveData<>(false);
        updateTaskList();
    }

    public MutableLiveData<List<Task>> getTaskList() {
        return taskList;
    }

    public MutableLiveData<Task> getSelectedTask() {
        return selectedTask;
    }

    public MutableLiveData<Boolean> getIsRolling() {
        return isRolling;
    }

    // 更新任务列表
    private void updateTaskList() {
        taskList.setValue(repository.getAllTasks());
    }

    // 添加任务
    public void addTask(String name, String description) {
        if (name != null && !name.trim().isEmpty()) {
            repository.addTask(name.trim(), description != null ? description.trim() : "");
            updateTaskList();
        }
    }

    // 删除任务
    public void removeTask(int position) {
        repository.removeTask(position);
        updateTaskList();
    }

    // 清空所有任务
    public void clearAllTasks() {
        repository.clearAllTasks();
        updateTaskList();
        selectedTask.setValue(null);
    }

    // 标记任务完成
    public void markTaskCompleted(int position) {
        repository.markTaskCompleted(position);
        updateTaskList();
    }

    // 重置任务完成状态
    public void resetTaskCompletion(int position) {
        repository.resetTaskCompletion(position);
        updateTaskList();
    }

    // 开始随机选择
    public void startRandomSelection(boolean avoidRepeat) {
        isRolling.setValue(true);
        selectedTask.setValue(null);
    }

    // 停止随机选择 - 修复：移除重复计数
    public void stopRandomSelection(boolean avoidRepeat) {
        isRolling.setValue(false);
        Task selected = repository.pickRandomTask(avoidRepeat);
        selectedTask.setValue(selected);
        updateTaskList(); // 更新列表以显示新的抽取次数
    }
}