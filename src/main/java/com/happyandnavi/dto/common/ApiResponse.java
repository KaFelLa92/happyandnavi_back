package com.happyandnavi.dto.common;

import lombok.*;
import java.time.LocalDateTime;

/**
 * =========================================
 * 공통 API 응답 DTO
 * =========================================
 * 
 * 모든 API 응답의 일관된 형식을 위한 래퍼 클래스입니다.
 * RESTful API 응답 표준화에 사용됩니다.
 * 
 * @param <T> 응답 데이터의 타입
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    /**
     * 응답 성공 여부
     * true: 성공, false: 실패
     */
    private boolean success;
    
    /**
     * 응답 메시지
     * 성공 또는 실패에 대한 설명
     */
    private String message;
    
    /**
     * 응답 데이터
     * 실제 반환할 데이터 (제네릭 타입)
     */
    private T data;
    
    /**
     * HTTP 상태 코드
     * 200: OK, 201: Created, 400: Bad Request, 401: Unauthorized, 등
     */
    private int statusCode;
    
    /**
     * 응답 시간
     * API 응답이 생성된 시간
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // ========================================
    // 정적 팩토리 메서드
    // ========================================
    
    /**
     * 성공 응답 생성 (데이터 포함)
     * 
     * @param data 응답 데이터
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(200)
                .build();
    }
    
    /**
     * 성공 응답 생성 (데이터만)
     * 
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "요청이 성공적으로 처리되었습니다.");
    }
    
    /**
     * 성공 응답 생성 (메시지만)
     * 
     * @param message 성공 메시지
     * @return 성공 응답 객체
     */
    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .statusCode(200)
                .build();
    }
    
    /**
     * 생성 성공 응답 (201 Created)
     * 
     * @param data 생성된 데이터
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return 생성 성공 응답 객체
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(201)
                .build();
    }
    
    /**
     * 실패 응답 생성
     * 
     * @param message 실패 메시지
     * @param statusCode HTTP 상태 코드
     * @param <T> 데이터 타입
     * @return 실패 응답 객체
     */
    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .build();
    }
    
    /**
     * 400 Bad Request 응답
     * 
     * @param message 오류 메시지
     * @param <T> 데이터 타입
     * @return 실패 응답 객체
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(message, 400);
    }
    
    /**
     * 401 Unauthorized 응답
     * 
     * @param message 오류 메시지
     * @param <T> 데이터 타입
     * @return 실패 응답 객체
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(message, 401);
    }
    
    /**
     * 403 Forbidden 응답
     * 
     * @param message 오류 메시지
     * @param <T> 데이터 타입
     * @return 실패 응답 객체
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(message, 403);
    }
    
    /**
     * 404 Not Found 응답
     * 
     * @param message 오류 메시지
     * @param <T> 데이터 타입
     * @return 실패 응답 객체
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(message, 404);
    }
    
    /**
     * 500 Internal Server Error 응답
     * 
     * @param message 오류 메시지
     * @param <T> 데이터 타입
     * @return 실패 응답 객체
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(message, 500);
    }
}
