package com.example.studybro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class DiaryActivity extends AppCompatActivity {

    private ImageView moodImageView;
    private EditText diaryEditText;
    private Button buttonSaveDiary;

    // 定义 15 张图片资源
    private int[] moodImages = {
            R.drawable.happy, R.drawable.sad, R.drawable.neutral, R.drawable.angry, R.drawable.bored,
            R.drawable.nervous, R.drawable.relax, R.drawable.satisfy, R.drawable.expect, R.drawable.like,
            R.drawable.afraid, R.drawable.awkward, R.drawable.tired, R.drawable.amazed, R.drawable.amazed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        moodImageView = findViewById(R.id.mood_image_view);
        diaryEditText = findViewById(R.id.diary_edit_text);
        buttonSaveDiary = findViewById(R.id.buttonSaveDiary);

        // 获取传递的心情图片索引
        int moodImageIndex = getIntent().getIntExtra("moodImage", -1);

        // 根据传递的索引显示相应的心情图片
        if (moodImageIndex >= 0 && moodImageIndex < moodImages.length) {
            // 使用数组来设置图片
            moodImageView.setImageResource(moodImages[moodImageIndex]);
        } else {
            // 如果索引不合法，设置默认图片
            moodImageView.setImageResource(R.drawable.happy);
        }
        // 点击“保存日记”按钮
        buttonSaveDiary.setOnClickListener(v -> {
            String diaryText = diaryEditText.getText().toString().trim();

            if (diaryText.isEmpty()) {
                Toast.makeText(this, "内容为空，也会保存~", Toast.LENGTH_SHORT).show();
            }

            long entryId = saveDiary(moodImageIndex, diaryText);

            Intent intent = new Intent(DiaryActivity.this, DiaryResultActivity.class);
            intent.putExtra("moodIndex", moodImageIndex);
            intent.putExtra("diaryText", diaryText);
            intent.putExtra("entryId", entryId);   // ⭐ 把ID一起传过去
            startActivity(intent);
        });


    }
    // 保存日记，返回这条日记的唯一ID（时间戳）
    private long saveDiary(int moodIndex, String text) {
        SharedPreferences sp = getSharedPreferences("DiaryPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        long ts = System.currentTimeMillis(); // 唯一ID
        String id = String.valueOf(ts);

        editor.putInt("mood_" + id, moodIndex);
        editor.putString("text_" + id, text);

        // 维护 all_ids 列表
        Set<String> idSet = sp.getStringSet("all_ids", new HashSet<>());
        Set<String> newSet = new HashSet<>(idSet);
        newSet.add(id);
        editor.putStringSet("all_ids", newSet);

        editor.apply();
        return ts;
    }



}
