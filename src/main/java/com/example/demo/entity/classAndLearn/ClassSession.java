package com.example.demo.entity.classAndLearn;

import com.example.demo.constant.StatusClassSession;
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
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate date;
    private String topic;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusClassSession statusClassSession;

    @ManyToOne
    @JoinColumn(name = "classShedule_id")
    @NotNull
    private ClassSchedule classSchedule;

    @OneToMany(mappedBy = "classSession")
    private List<Attendance> attendances;

}

