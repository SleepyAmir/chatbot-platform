package com.example.platform.modules.course.service;

import com.example.platform.modules.course.dto.request.CourseRequest;
import com.example.platform.modules.course.dto.response.CourseResponse;
import com.example.platform.modules.course.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {

    List<CourseResponse> getAllCourses();

    Page<CourseResponse> getAllCourses(Pageable pageable);

    Page<CourseResponse> searchCourses(String keyword, Pageable pageable);

    CourseResponse getCourseById(Integer id);

    CourseResponse getCourseByName(String name);

    CourseResponse createCourse(CourseRequest request);

    CourseResponse updateCourse(Integer id, CourseRequest request);

    void deleteCourse(Integer id);

    boolean existsById(Integer id);

    Course getRequiredCourseEntity(Integer id);
}