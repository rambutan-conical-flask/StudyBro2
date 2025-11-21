package com.example.studybro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AIChatActivity extends AppCompatActivity {
    private RecyclerView rvChat;
    private EditText etInput;
    private Button btnSend;
    private ChatAdapter adapter;
    private final List<ChatMessage> messageList = new ArrayList<>(); // 改为final

    // DeepSeek API配置
    private static final String DEEPSEEK_API_KEY = "sk-8e7066dfe5b4442eb61b8501e7e99cfe"; // 替换为你的实际密钥
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        // 初始化OkHttpClient
        client = new OkHttpClient();

        // 初始化控件
        rvChat = findViewById(R.id.rv_chat);
        etInput = findViewById(R.id.et_input);
        btnSend = findViewById(R.id.btn_send);

        // 初始化聊天列表
        adapter = new ChatAdapter(messageList);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // 发送按钮点击事件
        btnSend.setOnClickListener(v -> sendMessage());
    }

    // 发送消息
    private void sendMessage() {
        String content = etInput.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入消息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 添加用户消息到列表
        messageList.add(new ChatMessage(content, ChatMessage.TYPE_USER));
        adapter.notifyItemInserted(messageList.size() - 1);
        rvChat.scrollToPosition(messageList.size() - 1);
        etInput.setText("");

        // 调用DeepSeek API
        callDeepSeekAPI(content);
    }

    // 调用DeepSeek API
    // 调用DeepSeek API
    private void callDeepSeekAPI(String userInput) {
        // 显示加载中的消息
        ChatMessage loadingMessage = new ChatMessage("思考中...", ChatMessage.TYPE_AI);
        messageList.add(loadingMessage);
        int loadingPosition = messageList.size() - 1;
        adapter.notifyItemInserted(loadingPosition);
        rvChat.scrollToPosition(loadingPosition);

        try {
            // 构建请求体 - 简化版本
            String requestBody = "{" +
                    "\"model\": \"deepseek-chat\"," +
                    "\"messages\": [{\"role\": \"user\", \"content\": \"" + userInput + "\"}]," +
                    "\"max_tokens\": 1000" +
                    "}";

            System.out.println("=== 调试信息 ===");
            System.out.println("API密钥长度: " + DEEPSEEK_API_KEY.length());
            System.out.println("API密钥前10位: " + DEEPSEEK_API_KEY.substring(0, Math.min(10, DEEPSEEK_API_KEY.length())));
            System.out.println("请求内容: " + userInput);
            System.out.println("请求体: " + requestBody);

            RequestBody body = RequestBody.create(requestBody, JSON);

            Request httpRequest = new Request.Builder()
                    .url(DEEPSEEK_API_URL)
                    .header("Authorization", "Bearer " + DEEPSEEK_API_KEY)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            System.out.println("开始API调用...");

            // 执行API调用
            client.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("网络请求失败: " + e.getMessage());
                    runOnUiThread(() -> {
                        messageList.remove(loadingMessage);
                        adapter.notifyItemRemoved(loadingPosition);
                        String errorMsg = "网络错误: " + e.getMessage();
                        messageList.add(new ChatMessage(errorMsg, ChatMessage.TYPE_AI));
                        adapter.notifyItemInserted(messageList.size() - 1);
                        rvChat.scrollToPosition(messageList.size() - 1);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "null";

                    System.out.println("=== API响应 ===");
                    System.out.println("响应码: " + response.code());
                    System.out.println("响应体: " + responseBody);
                    System.out.println("响应头: " + response.headers());

                    runOnUiThread(() -> {
                        messageList.remove(loadingMessage);
                        adapter.notifyItemRemoved(loadingPosition);

                        if (response.isSuccessful() && responseBody != null && !responseBody.equals("null")) {
                            String aiReply = parseAIResponse(responseBody);
                            messageList.add(new ChatMessage(aiReply, ChatMessage.TYPE_AI));
                        } else {
                            String errorMsg;
                            if (responseBody.contains("invalid_api_key")) {
                                errorMsg = "API密钥无效，请检查密钥是否正确";
                            } else if (responseBody.contains("rate_limit")) {
                                errorMsg = "API调用频率超限";
                            } else if (response.code() == 401) {
                                errorMsg = "认证失败，API密钥错误";
                            } else if (response.code() == 429) {
                                errorMsg = "请求过于频繁，请稍后重试";
                            } else {
                                errorMsg = "请求失败: " + response.code() + " - " + responseBody;
                            }
                            messageList.add(new ChatMessage(errorMsg, ChatMessage.TYPE_AI));
                        }
                        adapter.notifyItemInserted(messageList.size() - 1);
                        rvChat.scrollToPosition(messageList.size() - 1);
                    });
                }
            });

        } catch (Exception e) {
            System.out.println("请求构建异常: " + e.getMessage());
            runOnUiThread(() -> {
                messageList.remove(loadingMessage);
                adapter.notifyItemRemoved(loadingPosition);
                String errorMsg = "请求构建失败: " + e.getMessage();
                messageList.add(new ChatMessage(errorMsg, ChatMessage.TYPE_AI));
                adapter.notifyItemInserted(messageList.size() - 1);
                rvChat.scrollToPosition(messageList.size() - 1);
            });
        }
    }


    // 手动构建请求JSON
    private String buildRequestBody(String userInput) {
        return "{" +
                "\"model\": \"deepseek-chat\"," +
                "\"messages\": [{\"role\": \"user\", \"content\": \"" + escapeJson(userInput) + "\"}]," +
                "\"temperature\": 0.7," +
                "\"max_tokens\": 2000" +
                "}";
    }

    // 转义JSON字符串中的特殊字符
    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String parseAIResponse(String responseBody) {
        try {
            // 使用Android自带的JSONObject解析
            org.json.JSONObject jsonResponse = new org.json.JSONObject(responseBody);
            org.json.JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                org.json.JSONObject firstChoice = choices.getJSONObject(0);
                org.json.JSONObject message = firstChoice.getJSONObject("message");
                String content = message.getString("content");
                return content;
            } else {
                return "AI没有返回有效回复";
            }
        } catch (Exception e) {
            // 如果解析失败，返回原始响应用于调试
            return "解析响应失败，原始响应: " + responseBody.substring(0, Math.min(200, responseBody.length())) + "...";
        }
    }

    // 聊天消息实体类
    static class ChatMessage {
        static final int TYPE_USER = 0;
        static final int TYPE_AI = 1;
        String content;
        int type;
        ChatMessage(String content, int type) {
            this.content = content;
            this.type = type;
        }
    }

    // 聊天列表适配器
    static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<ChatMessage> messages;
        ChatAdapter(List<ChatMessage> messages) { this.messages = messages; }

        @Override
        public int getItemViewType(int position) { return messages.get(position).type; }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == ChatMessage.TYPE_USER) {
                return new UserViewHolder(inflater.inflate(R.layout.item_chat_user, parent, false));
            } else {
                return new AIViewHolder(inflater.inflate(R.layout.item_chat_ai, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChatMessage message = messages.get(position);
            if (holder instanceof UserViewHolder) {
                ((UserViewHolder) holder).tvContent.setText(message.content);
            } else {
                ((AIViewHolder) holder).tvContent.setText(message.content);
            }
        }

        @Override
        public int getItemCount() { return messages.size(); }

        static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tvContent;
            UserViewHolder(@NonNull View itemView) {
                super(itemView);
                tvContent = itemView.findViewById(R.id.tv_user_content);
            }
        }

        static class AIViewHolder extends RecyclerView.ViewHolder {
            TextView tvContent;
            AIViewHolder(@NonNull View itemView) {
                super(itemView);
                tvContent = itemView.findViewById(R.id.tv_ai_content);
            }
        }
    }
}