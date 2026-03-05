package com.example.demo.entity.classAndLearn;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "class_schedule_slots", joinColumns = @JoinColumn(name = "class_schedule_id"))
    private List<ClassScheduleSlot> slots;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
    private String room;
}

