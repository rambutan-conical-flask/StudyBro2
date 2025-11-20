package com.example.studybro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Random;

public class TaskBoxActivity extends AppCompatActivity {

    private TaskBoxViewModel viewModel;
    private TaskAdapter adapter;

    private EditText etTaskName, etTaskDescription;
    private Button btnAdd, btnClear, btnStart, btnStop;
    private ImageButton btnBack;
    private CheckBox cbAvoidRepeat;
    private TextView tvSelectedTask, tvTaskDescription;
    private RecyclerView rvTasks;

    private Handler rollingHandler;
    private boolean isRolling = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_box);

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initViews() {
        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnAdd = findViewById(R.id.btnAdd);
        btnClear = findViewById(R.id.btnClear);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnBack = findViewById(R.id.btnBack);
        cbAvoidRepeat = findViewById(R.id.cbAvoidRepeat);
        tvSelectedTask = findViewById(R.id.tvSelectedTask);
        tvTaskDescription = findViewById(R.id.tvTaskDescription);
        rvTasks = findViewById(R.id.rvTasks);

        rollingHandler = new Handler();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(TaskBoxViewModel.class);

        // 观察任务列表变化
        viewModel.getTaskList().observe(this, tasks -> {
            if (adapter != null) {
                adapter.updateData(tasks);
            }
        });

        // 观察选中的任务
        viewModel.getSelectedTask().observe(this, task -> {
            if (task != null) {
                showSelectedTask(task);
            }
        });

        // 观察滚动状态
        viewModel.getIsRolling().observe(this, rolling -> {
            isRolling = rolling;
            btnStart.setEnabled(!rolling);
            btnStop.setEnabled(rolling);

            if (rolling) {
                startRollingAnimation();
                tvSelectedTask.setVisibility(View.GONE);
                tvTaskDescription.setVisibility(View.GONE);
            } else {
                stopRollingAnimation();
            }
        });
    }

    private void setupRecyclerView() {
        // 创建适配器并设置监听器
        adapter = new TaskAdapter(viewModel.getTaskList().getValue(),
                new TaskAdapter.OnTaskLongClickListener() {
                    @Override
                    public void onTaskLongClick(int position) {
                        showDeleteConfirmationDialog(position);
                    }
                },
                new TaskAdapter.OnTaskClickListener() {
                    @Override
                    public void onTaskClick(int position) {
                        toggleTaskCompletion(position);
                    }
                });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // 返回按钮
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 添加任务
        btnAdd.setOnClickListener(v -> {
            String name = etTaskName.getText().toString().trim();
            String description = etTaskDescription.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                viewModel.addTask(name, description);
                etTaskName.setText("");
                etTaskDescription.setText("");
            } else {
                Toast.makeText(this, "请输入任务名称", Toast.LENGTH_SHORT).show();
            }
        });

        // 清空列表
        btnClear.setOnClickListener(v -> showClearConfirmationDialog());

        // 开始随机
        btnStart.setOnClickListener(v -> {
            List<Task> tasks = viewModel.getTaskList().getValue();
            if (tasks != null && !tasks.isEmpty()) {
                viewModel.startRandomSelection(cbAvoidRepeat.isChecked());
            } else {
                Toast.makeText(this, "请先添加任务", Toast.LENGTH_SHORT).show();
            }
        });

        // 停止随机
        btnStop.setOnClickListener(v -> {
            viewModel.stopRandomSelection(cbAvoidRepeat.isChecked());
        });
    }

    // 显示删除确认对话框
    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除任务")
                .setMessage("确定要删除这个任务吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    viewModel.removeTask(position);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 切换任务完成状态
    private void toggleTaskCompletion(int position) {
        Task task = viewModel.getTaskList().getValue().get(position);
        if (task.isCompleted()) {
            viewModel.resetTaskCompletion(position);
        } else {
            viewModel.markTaskCompleted(position);
        }
    }

    private void showClearConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清空任务")
                .setMessage("确定要清空所有任务吗？")
                .setPositiveButton("清空", (dialog, which) -> {
                    viewModel.clearAllTasks();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 开始滚动动画
    private void startRollingAnimation() {
        final Runnable rollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRolling) {
                    List<Task> tasks = viewModel.getTaskList().getValue();
                    if (tasks != null && !tasks.isEmpty()) {
                        int randomIndex = random.nextInt(tasks.size());
                        Task randomTask = tasks.get(randomIndex);

                        animateRollingText(randomTask.getName());

                        rollingHandler.postDelayed(this, 150);
                    }
                }
            }
        };
        rollingHandler.post(rollingRunnable);
    }

    private void stopRollingAnimation() {
        // 停止滚动逻辑已经在ViewModel中处理
    }

    // 滚动文本动画
    private void animateRollingText(String text) {
        tvSelectedTask.setText(text);
        tvSelectedTask.setVisibility(View.VISIBLE);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvSelectedTask, "scaleX", 0.9f, 1.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvSelectedTask, "scaleY", 0.9f, 1.1f);

        scaleX.setDuration(100);
        scaleY.setDuration(100);

        scaleX.start();
        scaleY.start();
    }

    // 显示选中的任务（带动画效果）
    private void showSelectedTask(Task task) {
        tvSelectedTask.setText(task.getName());
        tvSelectedTask.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(task.getDescription())) {
            tvTaskDescription.setText(task.getDescription());
            tvTaskDescription.setVisibility(View.VISIBLE);
        } else {
            tvTaskDescription.setVisibility(View.GONE);
        }

        // 显示抽取次数信息
        String pickInfo = "已被抽取 " + task.getPickCount() + " 次";
        Toast.makeText(this, "选中任务: " + task.getName() + "\n" + pickInfo, Toast.LENGTH_LONG).show();

        // 创建选中动画
        ObjectAnimator colorAnim = ObjectAnimator.ofArgb(tvSelectedTask, "textColor",
                getColor(android.R.color.holo_blue_dark), getColor(android.R.color.black));
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvSelectedTask, "scaleX", 1.0f, 1.3f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvSelectedTask, "scaleY", 1.0f, 1.3f, 1.0f);

        colorAnim.setDuration(500);
        scaleX.setDuration(500);
        scaleY.setDuration(500);

        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);

        colorAnim.start();
        scaleX.start();
        scaleY.start();

        colorAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvSelectedTask.setTextColor(getColor(android.R.color.black));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rollingHandler != null) {
            rollingHandler.removeCallbacksAndMessages(null);
        }
    }
}