package com.example.platform.modules.course.service.impl;

import com.example.platform.common.exception.ResourceNotFoundException;
import com.example.platform.modules.course.dto.request.CourseDetailRequest;
import com.example.platform.modules.course.dto.response.CourseDetailResponse;
import com.example.platform.modules.course.model.Course;
import com.example.platform.modules.course.model.CourseDetail;
import com.example.platform.modules.course.mapper.CourseDetailMapper;
import com.example.platform.modules.course.repository.CourseDetailRepository;
import com.example.platform.modules.course.service.CourseDetailService;
import com.example.platform.modules.course.service.CourseService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseDetailServiceImpl implements CourseDetailService {

    private final CourseService courseService;
    private final CourseDetailRepository courseDetailRepository;
    private final CourseDetailMapper courseDetailMapper;
    private final EntityManager entityManager;

    @Override
    public CourseDetailResponse getDetailById(Integer id) {
        CourseDetail detail = courseDetailRepository.findWithCourseById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course detail not found with id: " + id
                ));

        return courseDetailMapper.toResponse(detail);
    }

    @Override
    public CourseDetailResponse getDetailByCourseId(Integer courseId) {
        return courseDetailMapper.toResponse(getRequiredDetailByCourseId(courseId));
    }

    @Override
    public Page<CourseDetailResponse> searchDetails(String keyword, Pageable pageable) {
        return courseDetailRepository.searchDetails(keyword, pageable)
                .map(courseDetailMapper::toResponse);
    }

    @Override
    @Transactional
    public CourseDetailResponse createDetail(Integer courseId, CourseDetailRequest request) {
        if (courseDetailRepository.existsByCourse_Id(courseId)) {
            throw new IllegalArgumentException(
                    "Course detail already exists for course id: " + courseId
            );
        }

        Course course = courseService.getRequiredCourseEntity(courseId);

        CourseDetail detail = courseDetailMapper.toEntity(request, course);
        CourseDetail savedDetail = courseDetailRepository.saveAndFlush(detail);

        entityManager.refresh(savedDetail);

        return courseDetailMapper.toResponse(savedDetail);
    }

    @Override
    @Transactional
    public CourseDetailResponse updateDetailByCourseId(Integer courseId, CourseDetailRequest request) {
        CourseDetail detail = getRequiredDetailByCourseId(courseId);

        courseDetailMapper.updateEntityFromRequest(request, detail);

        CourseDetail savedDetail = courseDetailRepository.saveAndFlush(detail);

        entityManager.refresh(savedDetail);

        return courseDetailMapper.toResponse(savedDetail);
    }

    @Override
    @Transactional
    public CourseDetailResponse upsertDetail(Integer courseId, CourseDetailRequest request) {
        Course course = courseService.getRequiredCourseEntity(courseId);

        CourseDetail detail = courseDetailRepository.findByCourse_Id(courseId)
                .orElseGet(() -> courseDetailMapper.toEntity(request, course));

        courseDetailMapper.updateEntityFromRequest(request, detail);

        CourseDetail savedDetail = courseDetailRepository.saveAndFlush(detail);

        entityManager.refresh(savedDetail);

        return courseDetailMapper.toResponse(savedDetail);
    }

    @Override
    @Transactional
    public void deleteDetailByCourseId(Integer courseId) {
        if (!courseDetailRepository.existsByCourse_Id(courseId)) {
            throw new ResourceNotFoundException(
                    "Course detail not found for course id: " + courseId
            );
        }

        courseDetailRepository.deleteByCourse_Id(courseId);
    }

    @Override
    public boolean existsByCourseId(Integer courseId) {
        return courseDetailRepository.existsByCourse_Id(courseId);
    }

    private CourseDetail getRequiredDetailByCourseId(Integer courseId) {
        return courseDetailRepository.findByCourse_Id(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course detail not found for course id: " + courseId
                ));
    }
}