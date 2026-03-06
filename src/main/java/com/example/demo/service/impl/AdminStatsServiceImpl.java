package com.example.demo.service.impl;

import com.example.demo.constant.EnrollmentStatus;
import com.example.demo.dto.response.AdminStatsResponse;
import com.example.demo.dto.response.MonthlyRevenueStatsResponse;
import com.example.demo.dto.response.MonthlyStatsResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SalerMonthlyRevenueResponse;
import com.example.demo.dto.response.SalerRevenueStatsResponse;
import com.example.demo.dto.response.SalerTransactionResponse;
import com.example.demo.dto.response.TopCourseResponse;
import com.example.demo.dto.response.TopSalerResponse;
import com.example.demo.entity.statistics.MonthlyBusinessStats;
import com.example.demo.entity.statistics.MonthlyRevenueStats;
import com.example.demo.entity.statistics.CourseStudentStats;
import com.example.demo.entity.statistics.SalerRevenueStats;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.SalerTransactionRepository;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ConsultationRequestRepository;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.MonthlyBusinessStatsRepository;
import com.example.demo.repository.MonthlyRevenueStatsRepository;
import com.example.demo.repository.CourseStudentStatsRepository;
import com.example.demo.repository.SalerRevenueStatsRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.k1.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseClassRepository courseClassRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private ConsultationRequestRepository consultationRequestRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private MonthlyBusinessStatsRepository monthlyBusinessStatsRepository;
    @Autowired
    private MonthlyRevenueStatsRepository monthlyRevenueStatsRepository;
    @Autowired
    private CourseStudentStatsRepository courseStudentStatsRepository;
    @Autowired
    private SalerRevenueStatsRepository salerRevenueStatsRepository;
    @Autowired
    private SalerTransactionRepository salerTransactionRepository;

    @Override
    public AdminStatsResponse getStats() {
        return AdminStatsResponse.builder()
                .userCount(userRepository.countUsersExcludingRole("ADMIN"))
                .courseClassCount(courseClassRepository.count())
                .courseCount(courseRepository.count())
                .documentCount(documentRepository.count())
                .studentCount(userRepository.countUsersByRole("STUDENT"))
                .teacherCount(userRepository.countUsersByRole("TEACHER"))
                .adminCount(userRepository.countUsersByRole("ADMIN"))
                .salerCount(userRepository.countUsersByRole("SALER"))
                .build();
    }

    @Override
    public MonthlyStatsResponse getMonthlyStats(int year, int month) {
        return calculateAndPersistMonthlyStats(year, month);
    }

    @Override
    public void refreshMonthlyStats(LocalDateTime eventTime) {
        if (eventTime == null) {
            return;
        }
        calculateAndPersistMonthlyStats(eventTime.getYear(), eventTime.getMonthValue());
    }

    @Override
    public java.util.List<MonthlyRevenueStatsResponse> getLatestMonthlyRevenueStats() {
        return monthlyRevenueStatsRepository.findTop5ByOrderByStatYearDescStatMonthDesc()
                .stream()
                .map(this::toMonthlyRevenueResponse)
                .toList();
    }

    @Override
    public java.util.List<TopCourseResponse> getTopCourses() {
        return courseStudentStatsRepository.findTop5ByOrderByTotalStudentsDesc()
                .stream()
                .map(this::toTopCourseResponse)
                .toList();
    }

    @Override
    public java.util.List<TopSalerResponse> getTopSalers() {
        return salerRevenueStatsRepository.findTop3ByOrderByTotalRevenueDesc()
                .stream()
                .map(this::toTopSalerResponse)
                .toList();
    }

    @Override
    public java.util.List<SalerRevenueStatsResponse> getAllSalerRevenueStats() {
        return salerRevenueStatsRepository.findAllByOrderByTotalRevenueDesc()
                .stream()
                .map(this::toSalerRevenueStatsResponse)
                .toList();
    }

    @Override
    public SalerRevenueStatsResponse getSalerRevenueStats(Long salerId) {
        SalerRevenueStats stats = salerRevenueStatsRepository.findByUserId(salerId)
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));
        return toSalerRevenueStatsResponse(stats);
    }

    @Override
    public SalerMonthlyRevenueResponse getSalerMonthlyRevenue(Long salerId, int year, int month) {
        java.time.LocalDateTime start = java.time.LocalDate.of(year, month, 1).atStartOfDay();
        java.time.LocalDateTime end = start.plusMonths(1);
        Long totalRevenue = salerTransactionRepository
                .sumAmountBySalerIdAndOccurredAtBetween(salerId, start, end);
        long totalDeals = salerTransactionRepository
                .findAllBySaler_IdAndOccurredAtBetweenOrderByOccurredAtDesc(salerId, start, end)
                .size();
        return SalerMonthlyRevenueResponse.builder()
                .salerId(salerId)
                .totalRevenue(totalRevenue != null ? totalRevenue : 0L)
                .totalDeals(totalDeals)
                .build();
    }

    @Override
//    public java.util.List<SalerTransactionResponse> getSalerTransactions(Long salerId, int year, int month) {
    public PageResponse<SalerTransactionResponse> getSalerTransactions(Long salerId, int page, int size) {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by("occurredAt").descending());
        org.springframework.data.domain.Page<com.example.demo.entity.sales.SalerTransaction> pageResult =
                salerTransactionRepository.findAllBySaler_IdOrderByOccurredAtDesc(salerId, pageable);
        java.util.List<SalerTransactionResponse> data = pageResult.getContent()
                .stream()
                .map(t -> SalerTransactionResponse.builder()
                        .studentName(t.getStudentName())
                        .courseName(t.getCourseName())
                        .amount(t.getAmount())
                        .occurredAt(t.getOccurredAt())
                        .build())
                .toList();
        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    private MonthlyStatsResponse calculateAndPersistMonthlyStats(int year, int month) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1);

        long totalRevenue = enrollmentRepository.sumRevenueByEnrolledAtBetweenExcludingStatus(
                start, end, EnrollmentStatus.DROPPED);
        long newStudentCount = userRepository.countUsersByRoleAndCreatedAtBetween(
                "STUDENT", start, end);

        long totalConsultations = consultationRequestRepository.countByCreatedAtBetween(start, end);
        double closeRate = totalConsultations == 0
                ? 0.0
                : (double) newStudentCount / (double) totalConsultations;

        java.time.LocalDate startDate = start.toLocalDate();
        java.time.LocalDate endDate = end.toLocalDate();
        long suspendedEnrollments = enrollmentRepository.countByUpdatedAtBetweenAndStatus(
                  startDate, endDate, EnrollmentStatus.SUSPENDED);
        long totalAttendance = attendanceRepository.count();
        double postponeRate = totalAttendance == 0
                ? 0.0
                : (double) suspendedEnrollments / (double) totalAttendance;

        Optional<MonthlyBusinessStats> existing =
                monthlyBusinessStatsRepository.findByStatYearAndStatMonth(year, month);
        MonthlyBusinessStats stats = existing.orElseGet(MonthlyBusinessStats::new);
        stats.setStatYear(year);
        stats.setStatMonth(month);
        stats.setTotalRevenue(totalRevenue);
        stats.setNewStudentCount(newStudentCount);
        stats.setCloseRate(closeRate);
        stats.setPostponeRate(postponeRate);
        monthlyBusinessStatsRepository.save(stats);

        return MonthlyStatsResponse.builder()
                .year(year)
                .month(month)
                .totalRevenue(totalRevenue)
                .newStudentCount(newStudentCount)
                .closeRate(closeRate)
                .postponeRate(postponeRate)
                .build();
    }

    private MonthlyRevenueStatsResponse toMonthlyRevenueResponse(MonthlyRevenueStats stats) {
        return MonthlyRevenueStatsResponse.builder()
                .year(stats.getStatYear())
                .month(stats.getStatMonth())
                .totalRevenue(stats.getTotalRevenue())
                .build();
    }

    private TopCourseResponse toTopCourseResponse(CourseStudentStats stats) {
        var course = stats.getCourse();
        return TopCourseResponse.builder()
                .courseId(course != null ? course.getId() : null)
                .courseCode(course != null ? course.getCode() : null)
                .courseName(course != null ? course.getName() : null)
                .totalStudents(stats.getTotalStudents())
                .build();
    }

    private TopSalerResponse toTopSalerResponse(SalerRevenueStats stats) {
        var saler = stats.getSaler();
        return TopSalerResponse.builder()
                .salerUserId(saler != null && saler.getUser() != null ? saler.getUser().getId() : null)
                .salerCode(saler != null ? saler.getCode() : null)
                .totalRevenue(stats.getTotalRevenue())
                .build();
    }

    private SalerRevenueStatsResponse toSalerRevenueStatsResponse(SalerRevenueStats stats) {
        var saler = stats.getSaler();
        Long salerId = saler != null ? saler.getId() : null;
        long totalDeals = 0L;
        if (salerId != null) {
            totalDeals = salerTransactionRepository.countBySaler_Id(salerId);
        }
        return SalerRevenueStatsResponse.builder()
                .salerId(salerId)
                .totalRevenue(stats.getTotalRevenue())
                .totalDeals(totalDeals)
                .build();
    }
}
