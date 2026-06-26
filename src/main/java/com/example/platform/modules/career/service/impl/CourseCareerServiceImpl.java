package com.example.platform.modules.career.service.impl;

import com.example.platform.modules.career.dto.request.CourseCareerRequest;
import com.example.platform.modules.career.dto.response.CourseCareerResponse;
import com.example.platform.modules.career.mapper.CareerMapper;
import com.example.platform.modules.career.model.Career;
import com.example.platform.modules.career.model.CourseCareer;
import com.example.platform.modules.career.repository.CourseCareerRepository;
import com.example.platform.modules.career.service.CareerService;
import com.example.platform.modules.career.service.CourseCareerService;
import com.example.platform.modules.course.model.Course;
import com.example.platform.modules.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseCareerServiceImpl implements CourseCareerService {

    private final CourseService courseService;
    private final CareerService careerService;
    private final CourseCareerRepository courseCareerRepository;
    private final CareerMapper careerMapper;

    @Override
    @Transactional
    public CourseCareerResponse linkCareerToCourse(Integer courseId, CourseCareerRequest request) {
        if (courseCareerRepository.existsByCourse_IdAndCareer_Id(courseId, request.careerId())) {
            throw new IllegalArgumentException("Career is already linked to course");
        }

        Course course = courseService.getRequiredCourseEntity(courseId);
        Career career = careerService.getRequiredCareerEntity(request.careerId());
        CourseCareer savedCourseCareer = courseCareerRepository.saveAndFlush(
                new CourseCareer(course, career, request.relevance())
        );

        return careerMapper.toCourseCareerResponse(savedCourseCareer);
    }

    @Override
    public List<CourseCareerResponse> getCareersByCourse(Integer courseId) {
        courseService.getRequiredCourseEntity(courseId);
        return courseCareerRepository.findByCourse_IdOrderByRelevanceDesc(courseId)
                .stream()
                .map(careerMapper::toCourseCareerResponse)
                .toList();
    }

    @Override
    public List<CourseCareerResponse> getCoursesByCareer(Integer careerId) {
        careerService.getRequiredCareerEntity(careerId);
        return courseCareerRepository.findByCareer_IdOrderByRelevanceDesc(careerId)
                .stream()
                .map(careerMapper::toCourseCareerResponse)
                .toList();
    }
}
