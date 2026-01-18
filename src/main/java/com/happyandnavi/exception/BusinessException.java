package com.happyandnavi.exception;

import lombok.Getter;

/**
 * =========================================
 * BusinessException 클래스
 * =========================================
 * 
 * 비즈니스 로직에서 발생하는 예외를 처리하기 위한 커스텀 예외 클래스입니다.
 * HTTP 상태 코드와 메시지를 함께 전달할 수 있습니다.
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * HTTP 상태 코드
     * 예: 400 (Bad Request), 401 (Unauthorized), 404 (Not Found) 등
     */
    private final int statusCode;
    
    /**
     * 기본 생성자 (500 Internal Server Error)
     * 
     * @param message 오류 메시지
     */
    public BusinessException(String message) {
        super(message);
        this.statusCode = 500;
    }
    
    /**
     * 상태 코드를 포함한 생성자
     * 
     * @param message 오류 메시지
     * @param statusCode HTTP 상태 코드
     */
    public BusinessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    /**
     * 원인 예외를 포함한 생성자
     * 
     * @param message 오류 메시지
     * @param cause 원인 예외
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }
    
    /**
     * 모든 정보를 포함한 생성자
     * 
     * @param message 오류 메시지
     * @param statusCode HTTP 상태 코드
     * @param cause 원인 예외
     */
    public BusinessException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
