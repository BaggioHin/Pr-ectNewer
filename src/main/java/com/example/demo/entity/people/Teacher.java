package com.example.demo.entity.people;

import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.classAndLearn.TeachingAssignment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    @Id
    private Long userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true, length = 50)
    private String teacherCode;

    @OneToMany(mappedBy = "teacher",cascade = CascadeType.ALL)
    private List<TeachingAssignment> teachingAssignments;

}


