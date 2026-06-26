package com.example.platform.modules.career.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CourseCareerId implements Serializable {

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "career_id")
    private Integer careerId;
}
