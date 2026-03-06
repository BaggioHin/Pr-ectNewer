package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error",HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    USERPROFILE_NOT_EXISTED(1008,"UserProfile not existed",HttpStatus.NOT_FOUND),
    USERNAME_EXISTED(1009,"Username existed",HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1010,"Email existed",HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_EXISTED(1011,"Phonenumber existed",HttpStatus.BAD_REQUEST),
    COURSECLASS_NOT_FOUND(1012,"CourseClass not found", HttpStatus.BAD_REQUEST),
    IMFORMATION_NULL(1013,"Imformation null",HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_FOUND(1014,"Enrollment not found",HttpStatus.BAD_REQUEST),
    SUBJECT_IN_USE(1015,"Subject has exams", HttpStatus.BAD_REQUEST),
    STUDENT_OR_COURSECLASS_NOT_EXIST(1016,"student or courseclass not exist",HttpStatus.NOT_FOUND),
    ENROLLMENT_EXISTED(1017,"Enrollment already exists", HttpStatus.BAD_REQUEST),
    COURSECLASS_CLOSED(1018,"CourseClass is closed", HttpStatus.BAD_REQUEST),
    COURSECLASS_FULL(1019,"CourseClass is full", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(1020,"Invalid status", HttpStatus.BAD_REQUEST),
    DOCUMENT_FILE_REQUIRED(1021, "Document file is required", HttpStatus.BAD_REQUEST),
    DOCUMENT_FILE_TYPE_NOT_SUPPORTED(1022, "Only PDF and DOCX files are supported", HttpStatus.BAD_REQUEST),
    DOCUMENT_TEXT_EXTRACTION_FAILED(1023, "Cannot extract text from file", HttpStatus.BAD_REQUEST),
    DOCUMENT_CONTENT_EMPTY(1024, "Document content is empty after extraction", HttpStatus.BAD_REQUEST),
    DOCUMENT_NOT_FOUND(1025, "Document not found", HttpStatus.NOT_FOUND),
    PASSWORD_CHANGE_NOT_ALLOWED(1026, "Password update is not allowed here", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT(1027, "Old password is incorrect", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_INVALID(1028, "Reset token is invalid", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_EXPIRED(1029, "Reset token has expired", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT(1030, "Invalid amount", HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
