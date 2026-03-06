package com.example.demo.service.impl;

import com.example.demo.constant.NotificationRefType;
import com.example.demo.constant.NotificationType;
import com.example.demo.constant.PaymentStatus;
import com.example.demo.constant.StatusCourse;
import com.example.demo.dto.request.VnpayCreateRequest;
import com.example.demo.dto.response.VnpayCreateResponse;
import com.example.demo.dto.response.VnpayReturnResponse;
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
import com.example.demo.service.k1.VnpayService;
import com.example.demo.service.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class VnpayServiceImpl implements VnpayService {

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.pay-url}")
    private String payUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.ipn-url}")
    private String ipnUrl;

    @Value("${vnpay.version:2.1.0}")
    private String version;

    @Value("${vnpay.command:pay}")
    private String command;

    @Value("${vnpay.locale:vn}")
    private String locale;

    @Value("${vnpay.order-type:other}")
    private String defaultOrderType;

    @Value("${vnpay.expire-minutes:15}")
    private long expireMinutes;

    private final CourseClassRepository courseClassRepository;
    private final UserRepository userRepository;
    private final SalerRepository salerRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public VnpayServiceImpl(CourseClassRepository courseClassRepository,
                            UserRepository userRepository,
                            SalerRepository salerRepository,
                            PaymentTransactionRepository paymentTransactionRepository,
                            EnrollmentRepository enrollmentRepository,
                            EnrollmentService enrollmentService,
                            EmailService emailService,
                            NotificationService notificationService) {
        this.courseClassRepository = courseClassRepository;
        this.userRepository = userRepository;
        this.salerRepository = salerRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentService = enrollmentService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Override
    public VnpayCreateResponse createPaymentUrl(VnpayCreateRequest request, String clientIp) {
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
        String txnRef = String.valueOf(System.currentTimeMillis());
        String orderInfo = request.getOrderInfo() != null
                ? request.getOrderInfo()
                : ("Payment for " + (courseClass.getName() != null ? courseClass.getName() : "class"));
        String orderType = request.getOrderType() != null ? request.getOrderType() : defaultOrderType;

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        String createDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String expireDate = now.plusMinutes(expireMinutes).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", version);
        params.put("vnp_Command", command);
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", orderType);
        params.put("vnp_Locale", locale);
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpnUrl", ipnUrl);
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        String queryString = buildQuery(params);
        String hashData = buildHashData(params);
        String secureHash = hmacSHA512(hashSecret, hashData);

        String paymentUrl = payUrl + "?" + queryString + "&vnp_SecureHash=" + secureHash;

        PaymentTransaction transaction = PaymentTransaction.builder()
                .txnRef(txnRef)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .courseClassId(request.getCourseClassId())
                .studentId(request.getStudentId())
                .salerUserId(request.getSalerUserId())
                .orderInfo(orderInfo)
                .paymentMethod("VNPAY")
                .build();
        paymentTransactionRepository.save(transaction);

        return VnpayCreateResponse.builder()
                .paymentUrl(paymentUrl)
                .txnRef(txnRef)
                .createDate(createDate)
                .build();
    }

    @Override
    public VnpayReturnResponse handleReturn(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            throw new AppException(ErrorCode.IMFORMATION_NULL);
        }
        boolean valid = verifySignature(params);
        Long amount = null;
        String rawAmount = params.get("vnp_Amount");
        if (rawAmount != null) {
            try {
                amount = Long.parseLong(rawAmount) / 100;
            } catch (NumberFormatException ignored) {
            }
        }
        return VnpayReturnResponse.builder()
                .validSignature(valid)
                .responseCode(params.get("vnp_ResponseCode"))
                .transactionStatus(params.get("vnp_TransactionStatus"))
                .txnRef(params.get("vnp_TxnRef"))
                .amount(amount)
                .orderInfo(params.get("vnp_OrderInfo"))
                .bankCode(params.get("vnp_BankCode"))
                .payDate(params.get("vnp_PayDate"))
                .build();
    }

    @Override
    public Map<String, String> handleIpn(Map<String, String> params) {
        Map<String, String> response = new LinkedHashMap<>();
        if (params == null || params.isEmpty()) {
            response.put("RspCode", "99");
            response.put("Message", "Invalid request");
            return response;
        }
        boolean valid = verifySignature(params);
        if (!valid) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid signature");
            return response;
        }

        String txnRef = params.get("vnp_TxnRef");
        if (txnRef == null) {
            response.put("RspCode", "99");
            response.put("Message", "Invalid request");
            return response;
        }
        var transactionOpt = paymentTransactionRepository.findByTxnRef(txnRef);
        if (transactionOpt.isEmpty()) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }
        PaymentTransaction transaction = transactionOpt.get();
        if (transaction.getStatus() == PaymentStatus.SUCCESS) {
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed");
            return response;
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String vnpTransactionNo = params.get("vnp_TransactionNo");
        String vnpPayDate = params.get("vnp_PayDate");
        String rawAmount = params.get("vnp_Amount");
        Long amount = null;
        if (rawAmount != null) {
            try {
                amount = Long.parseLong(rawAmount) / 100;
            } catch (NumberFormatException ignored) {
            }
        }
        if (amount == null || !amount.equals(transaction.getAmount())) {
            response.put("RspCode", "04");
            response.put("Message", "Invalid amount");
            return response;
        }

        transaction.setVnpResponseCode(responseCode);
        transaction.setVnpTransactionStatus(transactionStatus);
        transaction.setVnpTransactionNo(vnpTransactionNo);
        transaction.setVnpPayDate(vnpPayDate);

        if (!"00".equals(responseCode) || !"00".equals(transactionStatus)) {
            transaction.setStatus(PaymentStatus.FAILED);
            paymentTransactionRepository.save(transaction);
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
            return response;
        }

        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setConfirmedAt(LocalDateTime.now());
        paymentTransactionRepository.save(transaction);

        try {
            handleSuccess(transaction);
        } catch (Exception ex) {
            log.error("Failed to handle payment success for txnRef={}", transaction.getTxnRef(), ex);
        }

        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");
        return response;
    }

    private boolean verifySignature(Map<String, String> params) {
        String secureHash = params.get("vnp_SecureHash");
        if (secureHash == null) {
            return false;
        }
        Map<String, String> filtered = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if ("vnp_SecureHash".equals(key) || "vnp_SecureHashType".equals(key)) {
                continue;
            }
            filtered.put(key, entry.getValue());
        }
        String hashData = buildHashData(filtered);
        String computed = hmacSHA512(hashSecret, hashData);
        return secureHash.equalsIgnoreCase(computed);
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

    private String buildQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private String buildHashData(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(keySpec);
            byte[] hash = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
