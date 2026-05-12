package com.happyandnavi.dto.user;

import lombok.*;

/**
 * =========================================
 * 로그인 응답 DTO
 * =========================================
 * 
 * 로그인 성공 시 클라이언트에 반환하는 데이터를 담는 DTO입니다.
 * JWT Access Token과 Refresh Token을 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    /**
     * JWT Access Token
     * 
     * API 요청 시 Authorization 헤더에 포함하여 인증에 사용합니다.
     * 유효기간: 1시간 (설정에 따라 변경 가능)
     * 
     * 사용 예: Authorization: Bearer {accessToken}
     */
    private String accessToken;
    
    /**
     * JWT Refresh Token
     * 
     * Access Token 만료 시 새로운 Access Token을 발급받는 데 사용합니다.
     * 유효기간: 7일 (설정에 따라 변경 가능)
     * 
     * 보안을 위해 클라이언트에서 안전하게 저장해야 합니다.
     */
    private String refreshToken;
    
    /**
     * 토큰 타입
     * 항상 "Bearer"로 고정
     */
    @Builder.Default
    private String tokenType = "Bearer";
    
    /**
     * Access Token 만료 시간 (초 단위)
     * 클라이언트에서 토큰 갱신 타이밍을 계산하는 데 사용
     */
    private Long expiresIn;
    
    /**
     * 사용자 ID
     * 로그인한 사용자의 고유 번호
     */
    private Long userId;
    
    /**
     * 반려동물 이름 (프로필명)
     * 로그인 후 환영 메시지 등에 사용
     */
    private String petName;

    /**
     * 반려동물 프로필 사진
     */
    private String petPhotoUrl;

    /**
     * 사용자 이메일
     */
    private String email;
}
