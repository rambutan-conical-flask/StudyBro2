package com.example.studybro;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity implements TodoAdapter.OnItemActionListener {

    private EditText etTodoInput;
    private Button btnTodoAdd;
    private RecyclerView rvTodoList;

    private final ArrayList<TodoItem> todoItems = new ArrayList<>();
    private TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_todo);

        // 处理系统栏边距（根布局 id 是在 xml 里设置的 todo_root）
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.todo_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTodoInput = findViewById(R.id.todo_input);
        btnTodoAdd = findViewById(R.id.todo_add_btn);
        rvTodoList = findViewById(R.id.todo_list);

        rvTodoList.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(todoItems, this);
        rvTodoList.setAdapter(todoAdapter);

        btnTodoAdd.setOnClickListener(v -> {
            String text = etTodoInput.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                todoItems.add(0, new TodoItem(text));
                todoAdapter.notifyItemInserted(0);
                rvTodoList.scrollToPosition(0);
                etTodoInput.setText("");
            }
        });
    }

    @Override
    public void onToggle(int position) {
        if (position < 0 || position >= todoItems.size()) return;
        TodoItem item = todoItems.get(position);
        item.setDone(!item.isDone());
        todoAdapter.notifyItemChanged(position);
    }

    @Override
    public void onDelete(int position) {
        if (position < 0 || position >= todoItems.size()) return;
        todoItems.remove(position);
        todoAdapter.notifyItemRemoved(position);
    }
}