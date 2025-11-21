package com.example.studybro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studybro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiaryResultActivity extends AppCompatActivity {

    private ImageView imageMoodResult;
    private TextView textDiaryContent;
    private TextView textAiReply;
    Button buttonHistory;
    Button buttonToMain;
    private long entryId = -1;
    private static final String ZHIPU_API_KEY = "d7e7b0ab08ee48b88b4830cf371cabfc.oYCNd3tmLFXAWha0";   // 别对外泄露
    private static final String ZHIPU_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    // 与之前一致的图片数组
    private int[] moodImages = {
            R.drawable.happy, R.drawable.sad, R.drawable.neutral, R.drawable.angry, R.drawable.bored,
            R.drawable.nervous, R.drawable.relax, R.drawable.satisfy, R.drawable.expect, R.drawable.like,
            R.drawable.afraid, R.drawable.awkward, R.drawable.tired, R.drawable.amazed, R.drawable.amazed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_result);

        imageMoodResult = findViewById(R.id.imageMoodResult);
        textDiaryContent = findViewById(R.id.textDiaryContent);
        textAiReply = findViewById(R.id.textAiReply);

        buttonHistory = findViewById(R.id.buttonHistory);
        buttonToMain = findViewById(R.id.buttonToMain);

        // 1️⃣ 拿到从上一个页面传来的数据
        int moodIndex = getIntent().getIntExtra("moodIndex", -1);
        String diaryText = getIntent().getStringExtra("diaryText");
        entryId = getIntent().getLongExtra("entryId", -1);

        // 2️⃣ 显示心情图片和文本
        if (moodIndex >= 0 && moodIndex < moodImages.length) {
            imageMoodResult.setImageResource(moodImages[moodIndex]);
        }
        if (diaryText != null) {
            textDiaryContent.setText(diaryText);
        }
        //先检查本地有没有AI建议
        String cachedAi = loadCachedAiReply(entryId);
        if (cachedAi != null) {
            textAiReply.setText(cachedAi);  // 直接用缓存，不再请求
        } else {
            // 只在第一次、没缓存时调用AI
            requestAiReply(diaryText);
        }
        // 3️⃣ 调用 AI 接口，根据日记生成一段话
//        requestAiReply(diaryText);
        // 跳转到历史记录页面
        buttonHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DiaryResultActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // 跳转到主界面 SelectMoodActivity
        buttonToMain.setOnClickListener(v -> {
            Intent intent = new Intent(DiaryResultActivity.this, SelectMoodActivity.class);
            startActivity(intent);
        });
    }

    //使用okhttp发送请求
//    private OkHttpClient client = new OkHttpClient();
//    private static final MediaType JSON
//            = MediaType.get("application/json; charset=utf-8");

    // 从 SharedPreferences 读取 ai_<id>
    private String loadCachedAiReply(long id) {
        if (id == -1) return null;
        SharedPreferences sp = getSharedPreferences("DiaryPrefs", MODE_PRIVATE);
        return sp.getString("ai_" + id, null);
    }

    private void requestAiReply(String diaryText) {
        if (diaryText == null) diaryText = "";

        // 1️⃣ 组织提示语——你可以按自己需求改内容
        String prompt = "Please, in a gentle and encouraging tone, based on the following " +
                "diary entry, provide a short piece of comfort or advice (using English, no " +
                "more than 50 words)：\n\n" + diaryText;

        try {
            // 2️⃣ 用 JSONObject 来拼 JSON，比手搓字符串安全很多
            JSONObject root = new JSONObject();
            root.put("model", "glm-4.5");   // 或者 glm-4.6，看你在控制台开的是哪个

            // messages 数组
            JSONArray messages = new JSONArray();
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.put(userMsg);
            root.put("messages", messages);

            // thinking 参数（按示例开启）
            JSONObject thinking = new JSONObject();
            thinking.put("type", "enabled");
            root.put("thinking", thinking);

            // 其它可选参数
            root.put("max_tokens", 1024);   // 不一定要 4096，这里给个适中值
            root.put("temperature", 0.6);

            String jsonBody = root.toString();

            // 3️⃣ 构造 HTTP 请求
            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(ZHIPU_URL)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + ZHIPU_API_KEY)
                    .post(body)
                    .build();

            // 4️⃣ 子线程里发请求+把建议保存下来
            new Thread(() -> {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        final String aiText = parseAiText(responseStr);

                        // ⭐ 保存AI建议到本地，和这条日记绑定
                        cacheAiReply(entryId, aiText);

                        runOnUiThread(() -> textAiReply.setText(aiText));
                    } else {
                        final int code = response.code();
                        runOnUiThread(() ->
                                textAiReply.setText("AI 请求失败，错误码：" + code));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            textAiReply.setText("AI 请求出错：" + e.getMessage()));
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            textAiReply.setText("构造请求失败：" + e.getMessage());
        }
    }
    private void cacheAiReply(long id, String aiText) {
        if (id == -1) return;
        SharedPreferences sp = getSharedPreferences("DiaryPrefs", MODE_PRIVATE);
        sp.edit().putString("ai_" + id, aiText).apply();
    }


    /**
     * TODO: 按智谱返回的 JSON 结构解析出真正的内容
     * 这里只是占位，防止编译报错
     */
    private String parseAiText(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray choices = obj.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject first = choices.getJSONObject(0);
                JSONObject message = first.getJSONObject("message");
                String content = message.optString("content", "");
                if (content != null && !content.isEmpty()) {
                    return content;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "AI 返回内容解析失败，请稍后重试。";
    }


}
