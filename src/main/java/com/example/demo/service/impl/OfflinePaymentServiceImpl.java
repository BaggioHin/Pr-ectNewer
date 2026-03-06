package com.example.demo.service.impl;

import com.example.demo.constant.NotificationRefType;
import com.example.demo.constant.NotificationType;
import com.example.demo.constant.PaymentStatus;
import com.example.demo.constant.StatusCourse;
import com.example.demo.dto.request.OfflinePaymentConfirmRequest;
import com.example.demo.dto.request.OfflinePaymentCreateRequest;
import com.example.demo.dto.response.OfflinePaymentConfirmResponse;
import com.example.demo.dto.response.OfflinePaymentCreateResponse;
import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.payment.PaymentTransaction;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.CourseClassRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.PaymentTransactionRepository;
import com.example.demo.repository.SalerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.k1.EnrollmentService;
import com.example.demo.service.k1.OfflinePaymentService;
import com.example.demo.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OfflinePaymentServiceImpl implements OfflinePaymentService {

    @Autowired
    private CourseClassRepository courseClassRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SalerRepository salerRepository;
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationService notificationService;

    @Override
    public OfflinePaymentCreateResponse createOfflinePayment(OfflinePaymentCreateRequest request) {
        if (request == null || request.getCourseClassId() == null || request.getStudentId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (request.getSalerUserId() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        User studentUser = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (studentUser.getStudent() == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        if (salerRepository.findByUser_Id(request.getSalerUserId()) == null) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        CourseClass courseClass = courseClassRepository.findById(request.getCourseClassId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSECLASS_NOT_FOUND));
        if (courseClass.getStatusCourse() != StatusCourse.OPEN) {
            throw new AppException(ErrorCode.COURSECLASS_CLOSED);
        }
        if (courseClass.getCourse() == null || courseClass.getCourse().getPrice() == null) {
            if (request.getAmount() == null) {
                throw new AppException(ErrorCode.INVALID_AMOUNT);
            }
        }
        Long amount = courseClass.getCourse() != null && courseClass.getCourse().getPrice() != null
                ? courseClass.getCourse().getPrice()
                : request.getAmount();
        if (request.getAmount() != null
                && courseClass.getCourse() != null
                && courseClass.getCourse().getPrice() != null
                && !request.getAmount().equals(courseClass.getCourse().getPrice())) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }
        if (amount == null || amount <= 0) {
            throw new AppException(ErrorCode.INVALID_AMOUNT);
        }

        String txnRef = "OFF" + UUID.randomUUID().toString().replace("-", "");
        String orderInfo = request.getOrderInfo() != null
                ? request.getOrderInfo()
                : ("Offline payment for " + (courseClass.getName() != null ? courseClass.getName() : "class"));

        PaymentTransaction transaction = PaymentTransaction.builder()
                .txnRef(txnRef)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .courseClassId(request.getCourseClassId())
                .studentId(request.getStudentId())
                .salerUserId(request.getSalerUserId())
                .orderInfo(orderInfo)
                .paymentMethod(request.getPaymentMethod())
                .paymentInstructions(request.getPaymentInstructions())
                .build();
        paymentTransactionRepository.save(transaction);

        String transferCode = "PAY-" + txnRef;
        return OfflinePaymentCreateResponse.builder()
                .txnRef(txnRef)
                .amount(amount)
                .orderInfo(orderInfo)
                .paymentMethod(request.getPaymentMethod())
                .paymentInstructions(request.getPaymentInstructions())
                .transferCode(transferCode)
                .status(transaction.getStatus())
                .build();
    }

    @Override
    public OfflinePaymentConfirmResponse confirmOfflinePayment(OfflinePaymentConfirmRequest request) {
        if (request == null || request.getTxnRef() == null || request.getTxnRef().isBlank()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        PaymentTransaction transaction = paymentTransactionRepository.findByTxnRef(request.getTxnRef())
                .orElseThrow(() -> new AppException(ErrorCode.IMFORMATION_NULL));

        enforceSalerOwnershipIfNeeded(transaction);

        if (transaction.getStatus() == PaymentStatus.SUCCESS) {
            return OfflinePaymentConfirmResponse.builder()
                    .success(true)
                    .txnRef(transaction.getTxnRef())
                    .confirmationCode(transaction.getConfirmationCode())
                    .status(transaction.getStatus())
                    .build();
        }

        String confirmationCode = "CONF-" + UUID.randomUUID().toString().replace("-", "");
        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setConfirmationCode(confirmationCode);
        transaction.setConfirmedBy(resolveUserId());
        transaction.setConfirmedAt(LocalDateTime.now());
        paymentTransactionRepository.save(transaction);

        handleSuccess(transaction);

        return OfflinePaymentConfirmResponse.builder()
                .success(true)
                .txnRef(transaction.getTxnRef())
                .confirmationCode(confirmationCode)
                .status(transaction.getStatus())
                .build();
    }

    private void enforceSalerOwnershipIfNeeded(PaymentTransaction transaction) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        boolean isSaler = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SALER".equals(a.getAuthority()));
        if (!isSaler) {
            return;
        }
        Long userId = resolveUserId();
        if (userId == null || !userId.equals(transaction.getSalerUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void handleSuccess(PaymentTransaction transaction) {
        if (transaction == null) {
            return;
        }
        Long courseClassId = transaction.getCourseClassId();
        Long studentId = transaction.getStudentId();
        Long salerUserId = transaction.getSalerUserId();

        if (courseClassId == null || studentId == null || salerUserId == null) {
            return;
        }

        if (!enrollmentRepository.existsByStudent_UserIdAndCourseClass_Id(studentId, courseClassId)) {
            enrollmentService.createEnrollmentForPayment(courseClassId, studentId, salerUserId);
        }

        CourseClass courseClass = courseClassRepository.findById(courseClassId).orElse(null);
        String className = courseClass != null ? courseClass.getName() : null;
        String courseName = courseClass != null && courseClass.getCourse() != null
                ? courseClass.getCourse().getName()
                : null;

        User student = userRepository.findById(studentId).orElse(null);
        User salerUser = userRepository.findById(salerUserId).orElse(null);
        String studentName = student != null && student.getProfile() != null
                ? student.getProfile().getName()
                : null;

        if (student != null && student.getEmail() != null && !student.getEmail().isBlank()) {
            emailService.sendEnrollmentSuccessEmail(student.getEmail(), className, courseName);
        }
        if (salerUser != null && salerUser.getEmail() != null && !salerUser.getEmail().isBlank()) {
            emailService.sendSalerPaymentSuccessEmail(salerUser.getEmail(), studentName, className, courseName);
        }

        List<Long> userIds = new java.util.ArrayList<>();
        if (studentId != null) {
            userIds.add(studentId);
        }
        if (salerUserId != null) {
            userIds.add(salerUserId);
        }
        List<User> admins = userRepository.findByRoleName("ADMIN");
        for (User admin : admins) {
            userIds.add(admin.getId());
        }

        String title = "Payment successful";
        String content = "Student " + (studentName != null ? studentName : "UNKNOWN")
                + " paid for class " + (className != null ? className : "UNKNOWN");
        notificationService.notifyUsers(
                title,
                content,
                NotificationType.PAYMENT_SUCCESS,
                NotificationRefType.PAYMENT,
                transaction.getId(),
                userIds
        );
    }

    private Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return jwt.getClaim("userId");
    }
}
