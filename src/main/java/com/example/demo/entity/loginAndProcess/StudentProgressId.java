package com.example.demo.entity.loginAndProcess;

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
public class StudentProgressId implements Serializable {

    @NotNull
    private Long studentId;
    @NotNull
    private Long courseClassId;
}

