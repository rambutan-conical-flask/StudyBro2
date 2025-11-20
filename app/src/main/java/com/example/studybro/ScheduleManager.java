package com.example.studybro;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.studybro.dao.CourseDao;

import java.util.ArrayList;
import java.util.List;

public class ScheduleManager {
    private static final String TAG = "ScheduleManager";
    private static ScheduleManager instance;
    private final CourseDao courseDao;

    private ScheduleManager(Context context) {
        CourseDatabase db = CourseDatabase.getInstance(context);
        courseDao = db.courseDao();

        Log.d(TAG, "初始化课程数量=" + courseDao.getAllCourses().size());

        if (courseDao.getAllCourses().isEmpty()) {
            List<Course> sampleCourses = new ArrayList<>();
            sampleCourses.add(new Course("高等数学", "张教授", "101教室", 1, 9, 11));
            sampleCourses.add(new Course("大学英语", "刘老师", "202教室", 1, 14, 16));
            sampleCourses.add(new Course("Java编程", "李老师", "303教室", 2, 9, 12));
            courseDao.insertAll(sampleCourses);

            Log.d(TAG, "插入示例课程后数量=" + courseDao.getAllCourses().size());
        }
    }

    public static synchronized ScheduleManager getInstance(Context context) {
        if (instance == null) {
            instance = new ScheduleManager(context.getApplicationContext());
        }
        return instance;
    }

    public LiveData<List<Course>> getAllCoursesLive() {
        return courseDao.getAllCoursesLive();
    }

    public LiveData<List<Course>> getCoursesByDayLive(int dayOfWeek) {
        return courseDao.getCoursesByDayLive(dayOfWeek);
    }

    // ✅ 添加课程前先检查是否冲突
    public long addCourse(Course course) {
        if (hasConflict(course)) {
            Log.w(TAG, "课程时间冲突，添加失败: " + course.getName());
            return -1; // 表示添加失败
        }

        long id = courseDao.insert(course);
        Log.d(TAG, "新增课程: " + course.getName() + "，ID=" + id);
        return id;
    }

    // ✅ 时间冲突检测
    public boolean hasConflict(Course newCourse) {
        List<Course> existingCourses = courseDao.getAllCourses();
        for (Course c : existingCourses) {
            if (c.getDayOfWeek() != newCourse.getDayOfWeek()) continue;

            int startA = c.getStartTime();
            int endA = c.getEndTime();
            int startB = newCourse.getStartTime();
            int endB = newCourse.getEndTime();

            if (startA < endB && startB < endA) {
                Log.d(TAG, "冲突课程: " + c.getName() + " 与 " + newCourse.getName());
                return true;
            }
        }
        return false;
    }

    public int updateCourse(Course course) {
        int rows = courseDao.update(course);
        Log.d(TAG, "更新课程: " + course.getName() + "，影响行数=" + rows);
        return rows;
    }

    public void deleteCourse(Course course) {
        courseDao.delete(course);
        Log.d(TAG, "删除课程: " + course.getName());
    }

    public int removeCourse(Course course) {
        int rows = courseDao.delete(course);
        Log.d(TAG, "删除课程: " + course.getName() + "，影响行数=" + rows);
        return rows;
    }
}
