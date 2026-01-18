package com.happyandnavi.domain;

import lombok.*;
import java.time.LocalDateTime;

/**
 * =========================================
 * User 도메인 클래스 (사용자 테이블)
 * =========================================
 * 
 * 회원가입/로그인/설정/권한 관리를 위한 사용자 정보를 담는 엔티티입니다.
 * 
 * DB 테이블: users
 * 
 * @Builder: 빌더 패턴을 통한 객체 생성 지원
 * @NoArgsConstructor: 기본 생성자 자동 생성 (MyBatis 매핑에 필요)
 * @AllArgsConstructor: 모든 필드를 포함한 생성자 자동 생성
 * @Getter/@Setter: getter/setter 메서드 자동 생성
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    
    // ========================================
    // PK 필드
    // ========================================
    
    /**
     * 사용자 고유 번호 (Primary Key)
     * AUTO_INCREMENT로 자동 증가
     */
    private Long userId;
    
    // ========================================
    // 기본 정보 필드
    // ========================================
    
    /**
     * 사용자 이메일 (로그인 ID로 사용)
     * NOT NULL, UNIQUE
     */
    private String email;
    
    /**
     * 비밀번호 (BCrypt 암호화 저장)
     * 소셜 로그인 사용자의 경우 null 가능
     */
    private String password;
    
    /**
     * 사용자 연락처
     * 형식: 010-1234-5678 또는 01012345678
     */
    private String phone;
    
    /**
     * 반려동물 이름 (프로필에 표시될 이름)
     * NOT NULL
     */
    private String userName;
    
    // ========================================
    // 설정 필드
    // ========================================
    
    /**
     * 일정 알림 설정
     * 1: 알림 켜짐 (기본값)
     * 0: 알림 꺼짐
     */
    @Builder.Default
    private Integer scheduleSet = 1;
    
    /**
     * 가입 방법 (SignupType enum과 매핑)
     * 1: 일반 회원가입 (기본값)
     * 2: 카카오 로그인
     * 3: 구글 로그인
     */
    @Builder.Default
    private Integer signupType = 1;
    
    /**
     * 사용자 권한 (Role enum과 매핑)
     * ROLE_USER: 일반 사용자
     * ROLE_ADMIN: 관리자
     */
    @Builder.Default
    private String role = "ROLE_USER";
    
    // ========================================
    // 공통 필드 (모든 테이블 공용)
    // ========================================
    
    /**
     * 상태 코드
     * 1: 활성 (기본값)
     * 0: 비활성 (탈퇴, 정지 등)
     */
    @Builder.Default
    private Integer status = 1;
    
    /**
     * 생성 일시
     * 레코드 생성 시 자동으로 현재 시간 저장
     */
    private LocalDateTime regDate;
    
    /**
     * 수정 일시
     * 레코드 수정 시 자동으로 현재 시간 갱신
     */
    private LocalDateTime modDate;
    
    // ========================================
    // 소셜 로그인 관련 필드
    // ========================================
    
    /**
     * 소셜 로그인 제공자 ID
     * 카카오/구글에서 제공하는 고유 사용자 ID
     */
    private String socialId;
    
    /**
     * Refresh Token (JWT)
     * 토큰 갱신에 사용되며, 로그아웃 시 null로 설정
     */
    private String refreshToken;
    
    // ========================================
    // Enum 정의
    // ========================================
    
    /**
     * 가입 방법 열거형
     */
    public enum SignupType {
        NORMAL(1),      // 일반 회원가입
        KAKAO(2),       // 카카오 로그인
        GOOGLE(3);      // 구글 로그인
        
        private final int value;
        
        SignupType(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    /**
     * 사용자 권한 열거형
     */
    public enum Role {
        ROLE_USER,      // 일반 사용자
        ROLE_ADMIN      // 관리자
    }
}
