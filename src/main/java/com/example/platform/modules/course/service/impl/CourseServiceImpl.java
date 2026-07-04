package com.example.platform.modules.course.service.impl;

import com.example.platform.common.exception.ResourceNotFoundException;
import com.example.platform.modules.course.dto.request.CourseRequest;
import com.example.platform.modules.course.dto.response.CourseResponse;
import com.example.platform.modules.course.model.Course;
import com.example.platform.modules.course.mapper.CourseMapper;
import com.example.platform.modules.course.repository.CourseRepository;
import com.example.platform.modules.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Override
    public Page<CourseResponse> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(courseMapper::toResponse);
    }

    @Override
    public Page<CourseResponse> searchCourses(String keyword, Pageable pageable) {
        return courseRepository.searchCourses(keyword, pageable)
                .map(courseMapper::toResponse);
    }

    @Override
    @Cacheable(cacheNames = "courses", key = "#id")
    public CourseResponse getCourseById(Integer id) {
        return courseMapper.toResponse(getRequiredCourseEntity(id));
    }

    @Override
    public CourseResponse getCourseByName(String name) {
        Course course = courseRepository.findByNameIgnoreCaseExact(name)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with name: " + name
                ));

        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "courses", allEntries = true)
    public CourseResponse createCourse(CourseRequest request) {
        validateCourseNameIsUnique(request.name());
        Course course = courseMapper.toEntity(request);
        Course savedCourse = courseRepository.saveAndFlush(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "courses", allEntries = true)
    public CourseResponse updateCourse(Integer id, CourseRequest request) {
        Course course = getRequiredCourseEntity(id);

        validateCourseNameIsUniqueForUpdate(request.name(), id);

        courseMapper.updateEntityFromRequest(request, course);

        Course savedCourse = courseRepository.saveAndFlush(course);

        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "courses",  allEntries = true)
    public void deleteCourse(Integer id) {
        Course course = getRequiredCourseEntity(id);
        courseRepository.delete(course);
    }

    @Override
    public boolean existsById(Integer id) {
        return courseRepository.existsById(id);
    }

    @Override
    public Course getRequiredCourseEntity(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Course not found with id: " + id
                ));
    }

    private void validateCourseNameIsUnique(String name) {
        if (courseRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException(
                    "Course with this name already exists: " + name
            );
        }
    }

    private void validateCourseNameIsUniqueForUpdate(String name, Integer currentCourseId) {
        if (courseRepository.existsByNameIgnoreCaseAndIdNot(name, currentCourseId)) {
            throw new IllegalArgumentException(
                    "Course with this name already exists: " + name
            );
        }
    }
}