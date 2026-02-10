package com.example.demo.entity.classAndLearn;

import com.example.demo.constant.StatusAttendance;
import com.example.demo.entity.people.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @EmbeddedId
    @NotNull
    private AttendanceId id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusAttendance statusAttendance;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    @NotNull
    private Student student;

    @ManyToOne
    @MapsId("classSessionId")
    @JoinColumn(name = "classSession_id")
    @NotNull
    private ClassSession classSession;

}

