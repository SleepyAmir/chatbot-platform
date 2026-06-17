package com.example.platform.modules.course.service;

import com.example.platform.modules.course.dto.request.CourseDetailRequest;
import com.example.platform.modules.course.dto.response.CourseDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseDetailService {

    CourseDetailResponse getDetailById(Integer id);

    CourseDetailResponse getDetailByCourseId(Integer courseId);

    Page<CourseDetailResponse> searchDetails(String keyword, Pageable pageable);

    CourseDetailResponse createDetail(Integer courseId, CourseDetailRequest request);

    CourseDetailResponse updateDetailByCourseId(Integer courseId, CourseDetailRequest request);

    CourseDetailResponse upsertDetail(Integer courseId, CourseDetailRequest request);

    void deleteDetailByCourseId(Integer courseId);

    boolean existsByCourseId(Integer courseId);
}