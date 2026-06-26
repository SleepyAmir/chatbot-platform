package com.example.platform.modules.career.service;

import com.example.platform.modules.career.dto.request.CourseCareerRequest;
import com.example.platform.modules.career.dto.response.CourseCareerResponse;

import java.util.List;

public interface CourseCareerService {

    CourseCareerResponse linkCareerToCourse(Integer courseId, CourseCareerRequest request);

    List<CourseCareerResponse> getCareersByCourse(Integer courseId);

    List<CourseCareerResponse> getCoursesByCareer(Integer careerId);
}
