package com.example.platform.modules.course.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.course.dto.CourseDetailRequest;
import com.example.platform.modules.course.dto.CourseDetailResponse;
import com.example.platform.modules.course.dto.CourseRequest;
import com.example.platform.modules.course.dto.CourseResponse;
import com.example.platform.modules.course.service.CourseDetailService;
import com.example.platform.modules.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseDetailService courseDetailService;

    @GetMapping("/all")
    public ApiResponse<List<CourseResponse>> getAllCoursesWithoutPagination() {
        return ApiResponse.ok(courseService.getAllCourses());
    }

    @GetMapping
    public ApiResponse<Page<CourseResponse>> getCourses(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        Page<CourseResponse> result = (keyword == null || keyword.isBlank())
                ? courseService.getAllCourses(pageable)
                : courseService.searchCourses(keyword, pageable);

        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getCourseById(@PathVariable Integer id) {
        return ApiResponse.ok(courseService.getCourseById(id));
    }

    @GetMapping("/by-name")
    public ApiResponse<CourseResponse> getCourseByName(@RequestParam String name) {
        return ApiResponse.ok(courseService.getCourseByName(name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        return ApiResponse.ok("Course created successfully", courseService.createCourse(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CourseResponse> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody CourseRequest request
    ) {
        return ApiResponse.ok("Course updated successfully", courseService.updateCourse(id, request));
    }

    // NOTE: was previously @ResponseStatus(NO_CONTENT) with no body. Switched to 200 OK
    // with an ApiResponse<Void> body so the response envelope is consistent across the
    // whole API. A 204 response is not supposed to carry a body, so it can't be combined
    // with ApiResponse. Flag this to the team since it's an API contract change.
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ApiResponse.ok("Course deleted successfully");
    }

    @GetMapping("/{id}/details")
    public ApiResponse<CourseDetailResponse> getCourseDetails(@PathVariable Integer id) {
        return ApiResponse.ok(courseDetailService.getDetailByCourseId(id));
    }

    @PostMapping("/{id}/details")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CourseDetailResponse> createCourseDetails(
            @PathVariable Integer id,
            @RequestBody CourseDetailRequest request
    ) {
        return ApiResponse.ok(
                "Course detail created successfully",
                courseDetailService.createDetail(id, request)
        );
    }

    @PutMapping("/{id}/details")
    public ApiResponse<CourseDetailResponse> upsertCourseDetails(
            @PathVariable Integer id,
            @RequestBody CourseDetailRequest request
    ) {
        return ApiResponse.ok(
                "Course detail saved successfully",
                courseDetailService.upsertDetail(id, request)
        );
    }

    @DeleteMapping("/{id}/details")
    public ApiResponse<Void> deleteCourseDetails(@PathVariable Integer id) {
        courseDetailService.deleteDetailByCourseId(id);
        return ApiResponse.ok("Course detail deleted successfully");
    }

    @GetMapping("/details/search")
    public ApiResponse<Page<CourseDetailResponse>> searchCourseDetails(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return ApiResponse.ok(courseDetailService.searchDetails(keyword, pageable));
    }
}