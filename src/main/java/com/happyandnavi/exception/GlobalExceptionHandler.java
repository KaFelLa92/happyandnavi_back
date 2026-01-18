package com.happyandnavi.exception;

import com.happyandnavi.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

/**
 * =========================================
 * GlobalExceptionHandler 클래스
 * =========================================
 * 
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 핸들러입니다.
 * 
 * @RestControllerAdvice: 모든 @RestController에 대해 예외 처리를 적용
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * BusinessException 처리
     * 비즈니스 로직에서 발생하는 예외
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(e.getMessage(), e.getStatusCode());
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }
    
    /**
     * Validation 예외 처리
     * @Valid 어노테이션 검증 실패 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        // 모든 검증 오류 메시지를 수집
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.error("Validation Error: {}", errorMessage);
        
        ApiResponse<Void> response = ApiResponse.badRequest(errorMessage);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * BindException 처리
     * 요청 파라미터 바인딩 실패 시 발생
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.error("Bind Error: {}", errorMessage);
        
        ApiResponse<Void> response = ApiResponse.badRequest(errorMessage);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 파일 업로드 크기 초과 예외 처리
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("파일 크기 초과: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.badRequest("파일 크기가 너무 큽니다. 최대 50MB까지 업로드 가능합니다.");
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("잘못된 인자: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.badRequest(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("서버 오류 발생: ", e);
        
        ApiResponse<Void> response = ApiResponse.serverError("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
