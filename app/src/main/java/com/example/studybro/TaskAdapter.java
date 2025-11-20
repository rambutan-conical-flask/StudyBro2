package com.example.studybro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskLongClickListener longClickListener;
    private OnTaskClickListener clickListener;

    public interface OnTaskLongClickListener {
        void onTaskLongClick(int position);
    }

    public interface OnTaskClickListener {
        void onTaskClick(int position);
    }

    public TaskAdapter(List<Task> tasks, OnTaskLongClickListener longListener, OnTaskClickListener clickListener) {
        this.tasks = tasks;
        this.longClickListener = longListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);

        // 点击标记完成/未完成
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onTaskClick(position);
            }
        });

        // 长按删除
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onTaskLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateData(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskName;
        private TextView tvTaskDescription;
        private TextView tvCompletionCount;
        private TextView tvPickCount; // 新增：显示抽取次数
        private View completionIndicator;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvCompletionCount = itemView.findViewById(R.id.tvCompletionCount);
            tvPickCount = itemView.findViewById(R.id.tvPickCount); // 初始化抽取次数视图
            completionIndicator = itemView.findViewById(R.id.completionIndicator);
        }

        public void bind(Task task) {
            tvTaskName.setText(task.getName());
            tvTaskDescription.setText(task.getDescription());
            tvCompletionCount.setText("完成" + task.getCompletionCount() + "次");
            tvPickCount.setText("抽取" + task.getPickCount() + "次"); // 显示抽取次数

            // 根据完成状态设置样式
            if (task.isCompleted()) {
                completionIndicator.setBackgroundColor(0xFF4CAF50); // 绿色
                tvTaskName.setAlpha(0.6f);
                tvTaskDescription.setAlpha(0.6f);
            } else {
                completionIndicator.setBackgroundColor(0xFFF44336); // 红色
                tvTaskName.setAlpha(1.0f);
                tvTaskDescription.setAlpha(1.0f);
            }
        }
    }
}