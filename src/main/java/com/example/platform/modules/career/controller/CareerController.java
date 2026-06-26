package com.example.platform.modules.career.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.common.web.PageableUtils;
import com.example.platform.modules.career.dto.request.CareerRequest;
import com.example.platform.modules.career.dto.request.CareerRequirementRequest;
import com.example.platform.modules.career.dto.request.CareerSearchRequest;
import com.example.platform.modules.career.dto.request.CareerSkillRequest;
import com.example.platform.modules.career.dto.request.CourseCareerRequest;
import com.example.platform.modules.career.dto.response.CareerRequirementResponse;
import com.example.platform.modules.career.dto.response.CareerResponse;
import com.example.platform.modules.career.dto.response.CareerSearchResponse;
import com.example.platform.modules.career.dto.response.CareerSkillResponse;
import com.example.platform.modules.career.dto.response.CourseCareerResponse;
import com.example.platform.modules.career.service.CareerRequirementService;
import com.example.platform.modules.career.service.CareerService;
import com.example.platform.modules.career.service.CareerSkillService;
import com.example.platform.modules.career.service.CourseCareerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CareerController {

    private static final Set<String> CAREER_SORTABLE_PROPERTIES = Set.of(
            "id", "title", "description", "sourceUrl", "createdAt"
    );

    private final CareerService careerService;
    private final CareerSkillService careerSkillService;
    private final CareerRequirementService careerRequirementService;
    private final CourseCareerService courseCareerService;

    @GetMapping("/careers/all")
    public ApiResponse<List<CareerResponse>> getAllCareersWithoutPagination() {
        return ApiResponse.ok(careerService.getAllCareers());
    }

    @GetMapping("/careers")
    public ApiResponse<Page<CareerResponse>> getCareers(@RequestParam(required = false) String keyword, Pageable pageable) {
        Pageable safePageable = PageableUtils.sanitizeSort(pageable, CAREER_SORTABLE_PROPERTIES);
        Page<CareerResponse> result = (keyword == null || keyword.isBlank())
                ? careerService.getAllCareers(safePageable)
                : careerService.searchCareers(keyword, safePageable);
        return ApiResponse.ok(result);
    }

    @GetMapping("/careers/{id}")
    public ApiResponse<CareerResponse> getCareerById(@PathVariable Integer id) {
        return ApiResponse.ok(careerService.getCareerById(id));
    }

    @PostMapping("/careers")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CareerResponse> createCareer(@Valid @RequestBody CareerRequest request) {
        return ApiResponse.ok("Career created successfully", careerService.createCareer(request));
    }

    @PutMapping("/careers/{id}")
    public ApiResponse<CareerResponse> updateCareer(@PathVariable Integer id, @Valid @RequestBody CareerRequest request) {
        return ApiResponse.ok("Career updated successfully", careerService.updateCareer(id, request));
    }

    @DeleteMapping("/careers/{id}")
    public ApiResponse<Void> deleteCareer(@PathVariable Integer id) {
        careerService.deleteCareer(id);
        return ApiResponse.ok("Career deleted successfully");
    }

    @GetMapping("/careers/{id}/skills")
    public ApiResponse<List<CareerSkillResponse>> getSkillsByCareer(@PathVariable Integer id) {
        return ApiResponse.ok(careerSkillService.getSkillsByCareer(id));
    }

    @PostMapping("/careers/{id}/skills")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CareerSkillResponse> addSkillToCareer(
            @PathVariable Integer id,
            @Valid @RequestBody CareerSkillRequest request
    ) {
        return ApiResponse.ok("Career skill created successfully", careerSkillService.addSkillToCareer(id, request));
    }

    @GetMapping("/careers/{id}/requirements")
    public ApiResponse<List<CareerRequirementResponse>> getRequirementsByCareer(@PathVariable Integer id) {
        return ApiResponse.ok(careerRequirementService.getRequirementsByCareer(id));
    }

    @PostMapping("/careers/{id}/requirements")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CareerRequirementResponse> addRequirementToCareer(
            @PathVariable Integer id,
            @Valid @RequestBody CareerRequirementRequest request
    ) {
        return ApiResponse.ok("Career requirement created successfully", careerRequirementService.addRequirement(id, request));
    }

    @PostMapping("/careers/search")
    public ApiResponse<List<CareerSearchResponse>> searchCareers(@Valid @RequestBody CareerSearchRequest request) {
        return ApiResponse.ok(careerRequirementService.searchSimilar(request));
    }

    @GetMapping("/courses/{courseId}/careers")
    public ApiResponse<List<CourseCareerResponse>> getCareersByCourse(@PathVariable Integer courseId) {
        return ApiResponse.ok(courseCareerService.getCareersByCourse(courseId));
    }

    @PostMapping("/courses/{courseId}/careers")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CourseCareerResponse> linkCareerToCourse(
            @PathVariable Integer courseId,
            @Valid @RequestBody CourseCareerRequest request
    ) {
        return ApiResponse.ok("Career linked to course successfully", courseCareerService.linkCareerToCourse(courseId, request));
    }

    @GetMapping("/careers/{id}/courses")
    public ApiResponse<List<CourseCareerResponse>> getCoursesByCareer(@PathVariable Integer id) {
        return ApiResponse.ok(courseCareerService.getCoursesByCareer(id));
    }
}
