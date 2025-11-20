package com.example.studybro.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.Update;
import androidx.room.Query;

import com.example.studybro.Course;

import java.util.List;

@Dao
public interface CourseDao {

    // 插入单个课程，返回插入的行ID
    @Insert
    long insert(Course course);

    // 批量插入课程
    @Insert
    long[] insertAll(List<Course> courses);

    // 更新课程
    @Update
    int update(Course course);

    // 删除课程
    @Delete
    int delete(Course course);

    // 查询所有课程（推荐用 LiveData，UI 自动更新）
    @Query("SELECT * FROM courses ORDER BY dayOfWeek ASC, startTime ASC")
    LiveData<List<Course>> getAllCoursesLive();


    // 按星期几查询课程（LiveData）
    @Query("SELECT * FROM courses WHERE dayOfWeek = :day ORDER BY startTime ASC")
    LiveData<List<Course>> getCoursesByDayLive(int day);

    // 如果你需要一次性查询（非 LiveData），可以加上：
    @Query("SELECT * FROM courses ORDER BY dayOfWeek ASC, startTime ASC")
    List<Course> getAllCourses();


    @Query("SELECT * FROM courses WHERE dayOfWeek = :day ORDER BY startTime ASC")
    List<Course> getCoursesByDay(int day);
}
