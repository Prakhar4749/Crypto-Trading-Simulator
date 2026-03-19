package com.prakhar.notification.exception;

import com.prakhar.common.dto.ApiResponse;
import com.prakhar.common.exception.BaseException;
import com.prakhar.common.exception.ErrorCode;
import com.prakhar.common.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a");
    private static final ZoneId IST = 
        ZoneId.of("Asia/Kolkata");

    private String timestamp() {
        return ZonedDateTime.now(IST)
          .format(FORMATTER) + " IST";
    }

    private ApiResponse.ErrorDetails buildError(
        String code, int status, 
        String path, List<String> details) {
        return ApiResponse.ErrorDetails.builder()
            .code(code)
            .status(status)
            .path(path)
            .timestamp(timestamp())
            .details(details)
            .build();
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex, HttpServletRequest request) {
        log.error(LogUtil.error("notification-service", request.getRequestURI(), request.getHeader("X-User-Id"), "[" + ex.getErrorCode() + "] " + ex.getMessage()), ex.getStatus().is5xxServerError() ? ex : null);
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.error(ex.getMessage(), buildError(ex.getErrorCode(), ex.getStatus().value(), request.getRequestURI(), null)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).collect(Collectors.toList());
        log.warn(LogUtil.warn("notification-service", request.getRequestURI(), request.getHeader("X-User-Id"), "Validation failed: " + details));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Validation failed. Please check your input.", buildError(ErrorCode.VALIDATION_ERROR, 400, request.getRequestURI(), details)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Required parameter missing: " + ex.getParameterName(), buildError(ErrorCode.INVALID_INPUT, 400, request.getRequestURI(), List.of("Missing: " + ex.getParameterName()))));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String detail = ex.getName() + ": expected " + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown type");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Invalid parameter type.", buildError(ErrorCode.INVALID_INPUT, 400, request.getRequestURI(), List.of(detail))));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error(LogUtil.error("notification-service", request.getRequestURI(), request.getHeader("X-User-Id"), "Unexpected error: " + ex.getMessage()), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred. Please try again later.", buildError(ErrorCode.INTERNAL_ERROR, 500, request.getRequestURI(), null)));
    }
}
