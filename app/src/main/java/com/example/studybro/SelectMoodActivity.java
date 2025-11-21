package com.example.studybro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelectMoodActivity extends AppCompatActivity {

    // 定义图片资源（15张图片）
    private String[] moodImages = {
            "happy", "sad", "neutral", "angry", "bored", "nervous", "relax", "satisfy",
            "expect", "like", "afraid", "awkward", "tired", "amazed", "amazed"
    };

    private int selectedImage = -1; // 当前选中的心情图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mood);

        // 设置顶部的文字
        TextView feelingText = findViewById(R.id.feelingText);
        feelingText.setText("NOW, I’m feeling...");

        // 设置顶部显示的选中图片
        ImageView selectedImageView = findViewById(R.id.selected_image);

        // 设置十五张图片的点击事件
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) gridLayout.getChildAt(i);

            // 动态获取图片资源
            String imageName = moodImages[i];
            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            imageView.setImageResource(resId); // 设置图片资源

            final int index = i; // 使用 final 变量以便在点击时引用
            imageView.setOnClickListener(v -> selectMood(index, selectedImageView));
        }
    }

    // 选择心情的处理逻辑
    private void selectMood(int imageIndex, ImageView selectedImageView) {
        selectedImage = imageIndex;  // 记录选中的心情图片

        // 设置选中的图片显示在顶部
        String imageName = moodImages[imageIndex];
        int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        selectedImageView.setImageResource(resId); // 更新顶部图片

        Toast.makeText(this,  imageName, Toast.LENGTH_SHORT).show();
    }

    // 跳转到 DiaryActivity 并传递选中的图片
    public void goToDiary(View view) {
        if (selectedImage == -1) {
            Toast.makeText(this, "Please choose your mood！", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, DiaryActivity.class);
            Toast.makeText(this, "Mood has been chosen！", Toast.LENGTH_SHORT).show();
            intent.putExtra("moodImage", selectedImage); // 传递选中的心情图片索引
            startActivity(intent);  // 启动 DiaryActivity
        }
    }
    public void goToHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);  // 启动 DiaryActivity

    }
    public void goToMainInterface(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);  // 启动 DiaryActivity

    }
}
