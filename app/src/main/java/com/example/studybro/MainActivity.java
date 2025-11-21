package com.example.studybro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTime;
    private TextView tvDate;
    private LinearLayout layoutCourse, layoutTask, layoutLedger, layoutTodo, layoutCountdown,layoutDiary;
    private LinearLayout layoutHome, layoutAi, layoutAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setClickListeners();
        updateTimeAndDate();


    }

    private void initViews() {
        tvTime = findViewById(R.id.tv_time);
        tvDate = findViewById(R.id.tv_date);

        layoutCourse = findViewById(R.id.layout_course);
        layoutTask = findViewById(R.id.layout_task);
        layoutLedger = findViewById(R.id.layout_ledger);
        layoutTodo = findViewById(R.id.layout_todo);
        layoutCountdown = findViewById(R.id.layout_countdown);

        layoutHome = findViewById(R.id.layout_home);
        layoutAi = findViewById(R.id.layout_ai);
        layoutAccount = findViewById(R.id.layout_account);
        layoutDiary = findViewById(R.id.layout_diary);

    }

    private void setClickListeners() {
        layoutCourse.setOnClickListener(this);
        layoutTask.setOnClickListener(this);
        layoutLedger.setOnClickListener(this);
        layoutTodo.setOnClickListener(this);
        layoutCountdown.setOnClickListener(this);
        layoutHome.setOnClickListener(this);
        layoutAi.setOnClickListener(this);
        layoutAccount.setOnClickListener(this);
        layoutDiary.setOnClickListener(this);

    }

    private void updateTimeAndDate() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String time = timeFormat.format(calendar.getTime());
        tvTime.setText(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日EEEE", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());
        tvDate.setText(date);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.layout_course) {
            //course schedule
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);

        } else if (id == R.id.layout_task) {
            // 任务盲盒
            Intent intent = new Intent(MainActivity.this, TaskBoxActivity.class);
            startActivity(intent);

        } else if (id == R.id.layout_ledger) {
            // 记账本
            Intent intent = new Intent(MainActivity.this, LedgerActivity.class);
            startActivity(intent);

        } else if (id == R.id.layout_todo) {
            // ✅ To Do List：跳转到TodoActivity
            Intent intent = new Intent(MainActivity.this, TodoActivity.class);
            startActivity(intent);

        } else if (id == R.id.layout_countdown) {
            // 启动计时器Activity
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(intent);
        } else if (id == R.id.layout_diary) {
            // 启动日记Activity
            Intent intent = new Intent(MainActivity.this, SelectMoodActivity.class);
            startActivity(intent);
        } else if (id == R.id.layout_home) {
            showToast("Home clicked");
        } else if (id == R.id.layout_ai) {
            Intent intent = new Intent(MainActivity.this, AIChatActivity.class);
            startActivity(intent);
        } else if (id == R.id.layout_account) {
            showToast("Account clicked");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}