//package com.example.demo.entity.people;
//
//import com.example.demo.constant.StatusStudent;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.*;
//import org.w3c.dom.Text;
//
//import java.time.LocalDate;
//
//@Getter
//@Setter
//@Entity
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class StudentStatusHistory {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    @NotNull
//    private StatusStudent statusStudent;
//
//    @NotNull
//    private LocalDate fromDate;
//    private LocalDate endDate;
//
//    @Size(max = 500)
//    private String reason;
//
//    @ManyToOne
//    @JoinColumn(name = "studentStatusHistory_id")
//    @NotNull
//    private Student student;
//}
