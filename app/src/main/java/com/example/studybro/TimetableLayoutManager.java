package com.example.studybro;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class TimetableLayoutManager extends RecyclerView.LayoutManager {
    private final int hourHeight;       // 每小时高度
    private final int timeColumnWidth;  // 时间列宽度
    private final int verticalOffset;   // 顶部偏移量
    private final int columnCount = 6;  // 星期列数（周一到周六）
    private final Context context;

    public TimetableLayoutManager(Context context, int hourHeight, int timeColumnWidth, int verticalOffset) {
        this.context = context;
        this.hourHeight = hourHeight;
        this.timeColumnWidth = timeColumnWidth;
        this.verticalOffset = verticalOffset;
        setAutoMeasureEnabled(true); // ✅ 关键：强制测量
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        int screenWidth = getWidth();
        int columnWidth = (screenWidth - timeColumnWidth) / columnCount;

        for (int i = 0; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);

            // 从 View 的 tag 中获取课程对象
            Course course = (Course) view.getTag();
            if (course == null) continue;

            // 计算位置和尺寸
            int dayIndex = course.getDayOfWeek() - 1;
            int left = timeColumnWidth + dayIndex * columnWidth;
            int top = (course.getStartTime() - 9) * hourHeight ;
            int width = columnWidth;
            int height = course.getDuration() * hourHeight;

            layoutDecoratedWithMargins(view, left, top, left + width, top + height);

            Log.d("LayoutManager", "课程=" + course.getName() +
                    " 星期=" + course.getDayOfWeek() +
                    " left=" + left + " top=" + top +
                    " width=" + width + " height=" + height);
        }
    }
}
