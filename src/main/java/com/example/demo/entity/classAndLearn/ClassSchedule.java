package com.example.demo.entity.classAndLearn;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.DayOfWeek;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "class_id", nullable = false, unique = true)
    @NotNull
    private CourseClass courseClass;

    @OneToMany(mappedBy = "classSchedule")
    private List<ClassSession> classSessions;

    @Enumerated(EnumType.STRING)
    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
    private String room;
}

