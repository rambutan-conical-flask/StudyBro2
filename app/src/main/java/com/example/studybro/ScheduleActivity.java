package com.example.studybro;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class ScheduleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // 绑定 Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_schedule);

        // 左侧返回按钮 → 回到主页面
        toolbar.setNavigationOnClickListener(v -> finish());

        // 加载课程表 Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ScheduleFragment())
                    .commit();
        }
    }
}
