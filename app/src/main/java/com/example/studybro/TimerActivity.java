package com.example.studybro;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Random;

public class TimerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private LinearLayout llCountdownInput;
    private TextInputEditText etCountdown;
    private Spinner spinnerUnit;
    private TextInputEditText etTarget;
    private TextView tvTimer;
    private TextView tvExtra;
    private MaterialButton btnStart;
    private MaterialButton btnReset;

    // 励志语录相关变量
    private TextView tvQuote;
    private MaterialButton btnRefreshQuote;
    private String[] motivationalQuotes = {
            "每一次努力都是未来的你在向现在的你求救",
            "今天不走，明天要跑",
            "学习时的苦痛是暂时的，未学到的痛苦是终生的",
            "只有比别人更早、更勤奋地努力，才能尝到成功的滋味",
            "此刻打盹，你将做梦；此刻学习，你将圆梦",
            "觉得为时已晚的时候，恰恰是最早的时候",
            "学习这件事，不是缺乏时间，而是缺乏努力",
            "幸福或许不排名次，但成功必排名次",
            "学习并不是人生的全部。但既然连人生的一部分也无法征服，还能做什么呢？",
            "请享受无法回避的痛苦",
            "只有比别人更早、更勤奋地努力，才能尝到成功的滋味",
            "谁也不能随随便便成功，它来自彻底的自我管理和毅力",
            "时间在流逝",
            "现在流的口水，将成为明天的眼泪",
            "狗一样地学，绅士一样地玩",
            "今天不想跑，所以才去跑，这才是长距离跑者的思维方式",
            "坚持最难，但成果也最大",
            "学习不是人生的全部，但连学习都征服不了，你还能做什么？",
            "即使现在，对手也不停地翻动书页",
            "没有艰辛，便无所获"
    };

    private boolean isRunning = false;
    private long totalSeconds = 0L;
    private long remainingSeconds = 0L;
    private int tomatoCount = 3;
    private long tomatoDuration = 25 * 60L;
    private long lockDuration = 60 * 60L;
    private int currentMode = 0;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            switch (currentMode) {
                case 0: handleCountdown(); break;
                case 1: handleStopwatch(); break;
                case 2: handleTomato(); break;
                case 3: handleLock(); break;
            }
            updateTimerUI();
            if (isRunning) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        initViews();
        setupListeners();
        updateUIByMode();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        llCountdownInput = findViewById(R.id.llCountdownInput);
        etCountdown = findViewById(R.id.etCountdown);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        etTarget = findViewById(R.id.etTarget);
        tvTimer = findViewById(R.id.tvTimer);
        tvExtra = findViewById(R.id.tvExtra);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);

        // 初始化励志语录相关视图
        tvQuote = findViewById(R.id.tvQuote);
        btnRefreshQuote = findViewById(R.id.btnRefreshQuote);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.time_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);

        // 设置刷新语录按钮的点击事件
        btnRefreshQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshQuote();

                // 按钮旋转动画
                v.animate()
                        .rotationBy(360)
                        .setDuration(500)
                        .start();
            }
        });

        // 初始显示随机语录
        refreshQuote();
    }

    private void setupListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentMode = tab.getPosition();
                resetTimer();
                updateUIByMode();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnStart.setOnClickListener(v -> {
            if (!validateInput()) return;
            if (isRunning) pauseTimer(); else startTimer();
        });

        btnReset.setOnClickListener(v -> resetTimer());
    }

    // 刷新励志语录的方法
    private void refreshQuote() {
        Random random = new Random();
        int randomIndex = random.nextInt(motivationalQuotes.length);
        String newQuote = motivationalQuotes[randomIndex];

        // 淡入淡出动画
        tvQuote.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tvQuote.setText(newQuote);
                        tvQuote.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .start();
                    }
                })
                .start();
    }

    private boolean validateInput() {
        if (currentMode == 0) {
            if (etCountdown.getText().toString().isEmpty()) {
                Toast.makeText(this, "请输入倒计时时长", Toast.LENGTH_SHORT).show();
                return false;
            }
            try {
                int duration = Integer.parseInt(etCountdown.getText().toString());
                if (duration <= 0) {
                    Toast.makeText(this, "时长必须大于0", Toast.LENGTH_SHORT).show();
                    return false;
                }
                totalSeconds = (spinnerUnit.getSelectedItemPosition() == 0) ?
                        (long) duration * 60 : duration;
                remainingSeconds = totalSeconds;
                return true;
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效数字", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (etTarget.getText().toString().isEmpty()) {
                Toast.makeText(this, "请输入专注目标", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    private void handleCountdown() {
        remainingSeconds--;
        if (remainingSeconds <= 0) {
            isRunning = false;
            btnStart.setText("开始");
            Toast.makeText(this, "倒计时结束！", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleStopwatch() {
        remainingSeconds++;
    }

    private void handleTomato() {
        remainingSeconds--;
        if (remainingSeconds <= 0) {
            tomatoCount--;
            if (tomatoCount > 0) {
                remainingSeconds = tomatoDuration;
                Toast.makeText(this, "完成1个番茄钟，剩余" + tomatoCount + "个", Toast.LENGTH_SHORT).show();
                tvExtra.setText("🍅×" + tomatoCount);
            } else {
                isRunning = false;
                btnStart.setText("开始");
                Toast.makeText(this, "所有番茄钟完成！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleLock() {
        remainingSeconds--;
        if (remainingSeconds <= 0) {
            isRunning = false;
            btnStart.setText("开始");
            Toast.makeText(this, "锁机时间结束！", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTimer() {
        isRunning = true;
        btnStart.setText("暂停");
        handler.post(timerRunnable);
    }

    private void pauseTimer() {
        isRunning = false;
        btnStart.setText("开始");
        handler.removeCallbacks(timerRunnable);
    }

    private void resetTimer() {
        pauseTimer();
        switch (currentMode) {
            case 0:
                if (etCountdown.getText().toString().isEmpty()) {
                    remainingSeconds = 0;
                } else {
                    try {
                        int duration = Integer.parseInt(etCountdown.getText().toString());
                        totalSeconds = (spinnerUnit.getSelectedItemPosition() == 0) ?
                                (long) duration * 60 : duration;
                        remainingSeconds = totalSeconds;
                    } catch (NumberFormatException e) {
                        remainingSeconds = 0;
                    }
                }
                break;
            case 1:
                remainingSeconds = 0;
                break;
            case 2:
                remainingSeconds = tomatoDuration;
                tomatoCount = 3;
                break;
            case 3:
                remainingSeconds = lockDuration;
                break;
        }
        updateTimerUI();
        updateUIByMode();
    }

    private void updateTimerUI() {
        long minutes = remainingSeconds / 60;
        long secs = remainingSeconds % 60;
        tvTimer.setText(String.format("%02d:%02d", minutes, secs));
    }

    private void updateUIByMode() {
        llCountdownInput.setVisibility(currentMode == 0 ? View.VISIBLE : View.GONE);
        switch (currentMode) {
            case 2:
                tvExtra.setText("🍅×" + tomatoCount);
                break;
            case 3:
                tvExtra.setText("🔒 锁机中");
                break;
            default:
                tvExtra.setText("");
                break;
        }
        btnStart.setText("开始");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }
}