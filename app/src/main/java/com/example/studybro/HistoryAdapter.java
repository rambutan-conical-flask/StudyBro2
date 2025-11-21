package com.example.studybro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<DiaryEntry> data;
    private int[] moodImages;

    // ① 定义一个回调接口
    public interface OnItemClickListener {
        void onItemClick(DiaryEntry entry);
    }

    private OnItemClickListener listener;

    // ② 提供设置监听的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public HistoryAdapter(List<DiaryEntry> data, int[] moodImages) {
        this.data = data;
        this.moodImages = moodImages;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_entry, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        DiaryEntry entry = data.get(position);

        holder.textDate.setText(formatDate(entry.id));
        holder.textContent.setText(entry.text);

        if (entry.moodIndex >= 0 && entry.moodIndex < moodImages.length) {
            holder.imageMood.setImageResource(moodImages[entry.moodIndex]);
        }

        // ③ 在这里给整条 item 加点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(entry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMood;
        TextView textDate;
        TextView textContent;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMood = itemView.findViewById(R.id.imageMood);
            textDate = itemView.findViewById(R.id.textDate);
            textContent = itemView.findViewById(R.id.textContent);
        }
    }

    // 把时间戳转成 “11月1日 20:30” 这种
    private String formatDate(long ts) {
        Date date = new Date(ts);
        // 你可以只显示日期："M月d日"；也可以带时间："M月d日 HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日 HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}
