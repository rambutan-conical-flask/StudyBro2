package com.example.studybro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerHistory;
    private HistoryAdapter adapter;
    private List<DiaryEntry> allEntries = new ArrayList<>();
    Button buttonToMain;

    // 和之前保持一致的图片数组
    private int[] moodImages = {
            R.drawable.happy, R.drawable.sad, R.drawable.neutral, R.drawable.angry, R.drawable.bored,
            R.drawable.nervous, R.drawable.relax, R.drawable.satisfy, R.drawable.expect, R.drawable.like,
            R.drawable.afraid, R.drawable.awkward, R.drawable.tired, R.drawable.amazed, R.drawable.amazed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        buttonToMain = findViewById(R.id.buttonToMain);

        buttonToMain.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, SelectMoodActivity.class);
            startActivity(intent);
        });

        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        allEntries = loadDiaryEntries();
        adapter = new HistoryAdapter(allEntries, moodImages);
        recyclerHistory.setAdapter(adapter);

        // ⭐ 关键：设置条目点击事件
        adapter.setOnItemClickListener(entry -> {
            // 跳到详情页，这里我用 DiaryResultActivity 做详情展示
            Intent intent = new Intent(HistoryActivity.this, DiaryResultActivity.class);
            intent.putExtra("moodIndex", entry.moodIndex);
            intent.putExtra("diaryText", entry.text);
            // 如果以后需要精确知道是哪一条，可以顺便把 id 也传过去
            intent.putExtra("entryId", entry.id);
            startActivity(intent);
        });
    }


    // 从 SharedPreferences 中读取“有文本内容的历史日记”
    private List<DiaryEntry> loadDiaryEntries() {
        SharedPreferences sp = getSharedPreferences("DiaryPrefs", MODE_PRIVATE);

        Set<String> idSet = sp.getStringSet("all_ids", new HashSet<>());
        List<DiaryEntry> list = new ArrayList<>();

        for (String id : idSet) {
            int mood = sp.getInt("mood_" + id, -1);
            String text = sp.getString("text_" + id, "");

            // 只要“有文本内容”的历史日记
//            if (text != null && !text.isEmpty() && mood != -1) {
            if (mood != -1) {
                long ts;
                try {
                    ts = Long.parseLong(id);
                } catch (NumberFormatException e) {
                    continue; // 防止解析失败
                }
                list.add(new DiaryEntry(ts, mood, text));
            }
        }

        // ✅ 按时间戳倒序：最新在最上面
        Collections.sort(list, (a, b) -> Long.compare(b.id, a.id));

        return list;
    }

}
