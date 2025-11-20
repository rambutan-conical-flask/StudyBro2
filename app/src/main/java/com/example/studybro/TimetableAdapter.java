package com.example.studybro;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.CourseViewHolder> {
    private Context context;
    private List<Course> courseList;
    private int hourHeight = 150; // 每小时高度（仅用于设置卡片高度）

    public TimetableAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    public void setCourses(List<Course> courses) {
        this.courseList = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timetable_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.courseName.setText(course.getName());
        holder.courseInfo.setText(course.getTeacher() + " • " + course.getRoom());
        holder.courseTime.setText(course.getStartTime() + ":00 - " + course.getEndTime() + ":00");

        // ✅ 只设置高度，位置和宽度交给 LayoutManager
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = course.getDuration() * hourHeight;
        holder.itemView.setLayoutParams(params);

        // ✅ 把课程对象挂到 View 上，供 LayoutManager 使用
        holder.itemView.setTag(course);

        // ✅ 设置背景色（可根据课程名或其他属性）
        holder.itemView.setBackgroundColor(getColorForCourse(course.getName()));

        // ✅ 点击事件：弹出课程详情
        holder.itemView.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(course.getName())
                    .setMessage("教师: " + course.getTeacher() +
                            "\n教室: " + course.getRoom() +
                            "\n时间: " + course.getStartTime() + ":00 - " + course.getEndTime() + ":00")


                    .setPositiveButton("删除", (dialog, which) -> {
                        removeCourse(position);
                    })
                    // 关闭按钮（注意这里要用 setNegativeButton）
                    .setNegativeButton("关闭", null)
                    .show();



        });
    }
    public void removeCourse(int position) {
        if (courseList != null && position >= 0 && position < courseList.size()) {
            Course course = courseList.get(position);
            // 从数据库删除
            ScheduleManager.getInstance(context).deleteCourse(course);
            // 从列表删除并刷新
            courseList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return courseList == null ? 0 : courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseTime, courseInfo;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            courseTime = itemView.findViewById(R.id.course_time);
            courseInfo = itemView.findViewById(R.id.course_info);
        }
    }

    // 简单的颜色选择逻辑（你可以改成更复杂的）
    private int getColorForCourse(String name) {
        int hash = name.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = (hash & 0x0000FF);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    // ✅ 时间轴装饰器（画横线）
    public static class TimeLineDecoration extends RecyclerView.ItemDecoration {
        private final int hourHeight;
        private final int timeColumnWidth;
        private final int columnCount = 6; // 周一到周六
        private final Paint linePaint;
        private final Paint textPaint;
        private final Paint currentTimePaint;

        public TimeLineDecoration(Context context, int hourHeight) {
            this.hourHeight = hourHeight;
            this.timeColumnWidth = 200; // 时间列宽度要和 LayoutManager 一致

            linePaint = new Paint();
            linePaint.setColor(Color.LTGRAY);
            linePaint.setStrokeWidth(2);

            textPaint = new Paint();
            textPaint.setColor(Color.DKGRAY);
            textPaint.setTextSize(30);

            currentTimePaint = new Paint();
            currentTimePaint.setColor(Color.RED);
            currentTimePaint.setStrokeWidth(4);
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int width = parent.getWidth();
            int height = parent.getHeight();
            int columnWidth = (width - timeColumnWidth) / columnCount;

            // 画横线（时间刻度）
            for (int hour = 9; hour <= 22; hour++) {
                int y = (hour - 9) * hourHeight;
                canvas.drawLine(0, y, width, y, linePaint);
                canvas.drawText(hour + ":00", 20, y + 40, textPaint);
            }

            // 画竖线（星期分隔）
            for (int day = 0; day <= columnCount; day++) {
                int x = timeColumnWidth + day * columnWidth;
                canvas.drawLine(x, 0, x, height, linePaint);
            }

            // 画左边时间列竖线
            canvas.drawLine(timeColumnWidth, 0, timeColumnWidth, height, linePaint);

            // ✅ 画当前时间红线
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // 计算红线纵向位置（支持分钟级）
            if (hour >= 9 && hour <= 22) {
                float y = (hour - 9) * hourHeight + (minute / 60f) * hourHeight;
                canvas.drawLine(0, y, width, y, currentTimePaint);
            }
        }
    }

}
