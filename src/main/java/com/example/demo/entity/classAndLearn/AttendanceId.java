package com.example.demo.entity.classAndLearn;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceId implements Serializable {
    @NotNull
    private Long classSessionId;
    @NotNull
    private Long studentId;
}

