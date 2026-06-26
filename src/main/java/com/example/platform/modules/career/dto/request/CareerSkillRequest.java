package com.example.platform.modules.career.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CareerSkillRequest(
        @NotBlank(message = "Skill name is required")
        String skillName
) {
}
