package com.example.studybro;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {
    private RecyclerView timetableRecycler;
    private TimetableAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        timetableRecycler = view.findViewById(R.id.timetable_recycler);
        initRecyclerView();

        // 绑定 Add 按钮
        Button addCourseBtn = view.findViewById(R.id.add_course_btn);
        addCourseBtn.setOnClickListener(v -> showAddCourseDialog());

        return view;
    }

    private void initRecyclerView() {
        // 初始化 Adapter（先传空列表，后面用 LiveData 更新）
        adapter = new TimetableAdapter(requireContext(), new ArrayList<>());
        timetableRecycler.setAdapter(adapter);

        // 使用自定义 LayoutManager（负责摆放课程卡片）
        TimetableLayoutManager layoutManager =
                new TimetableLayoutManager(requireContext(), 150, 200, 50);
        timetableRecycler.setLayoutManager(layoutManager);

        // 添加时间轴装饰器（画横线）
        timetableRecycler.addItemDecoration(
                new TimetableAdapter.TimeLineDecoration(requireContext(), 150));

        // 观察数据库课程数据（实时刷新）
        ScheduleManager.getInstance(requireContext())
                .getAllCoursesLive()
                .observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
                    @Override
                    public void onChanged(List<Course> courses) {
                        Log.d("ScheduleFragment", "观察到课程数量=" + courses.size());
                        adapter.setCourses(courses);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void showAddCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("新增课程");

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_course, null);
        builder.setView(dialogView);

        EditText etName   = dialogView.findViewById(R.id.et_course_name);
        EditText etRoom   = dialogView.findViewById(R.id.et_course_room);
        Spinner spinnerDay   = dialogView.findViewById(R.id.spinner_day);
        Spinner spinnerStart = dialogView.findViewById(R.id.spinner_start_time);
        Spinner spinnerEnd   = dialogView.findViewById(R.id.spinner_end_time);

        // 星期几选项
        String[] days = {"周一","周二","周三","周四","周五","周六","周日"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                days
        );
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // 时间选项（9点到22点）
        List<String> timeOptions = new ArrayList<>();
        for (int i = 9; i <= 22; i++) {
            timeOptions.add(i + ":00");
        }
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                timeOptions
        );
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStart.setAdapter(timeAdapter);
        spinnerEnd.setAdapter(timeAdapter);

        builder.setPositiveButton("添加", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String room = etRoom.getText().toString().trim();
            int day = spinnerDay.getSelectedItemPosition() + 1; // 周一=1
            int start = spinnerStart.getSelectedItemPosition() + 9;
            int end   = spinnerEnd.getSelectedItemPosition() + 9;

            if (name.isEmpty() || room.isEmpty()) {
                Toast.makeText(requireContext(), "请填写课程名称和教室", Toast.LENGTH_SHORT).show();
                return;
            }
            if (end <= start) {
                Toast.makeText(requireContext(), "结束时间必须晚于开始时间", Toast.LENGTH_SHORT).show();
                return;
            }

            Course course = new Course(name, "", room, day, start, end);
            ScheduleManager manager = ScheduleManager.getInstance(requireContext());

            // ✅ 冲突检测
            if (manager.hasConflict(course)) {
                Toast.makeText(requireContext(), "课程时间冲突，无法添加", Toast.LENGTH_SHORT).show();
                return;
            }

            manager.addCourse(course);
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

}
