package com.happyandnavi.service;

import com.happyandnavi.domain.User;
import com.happyandnavi.dto.user.*;

import java.util.Optional;

/**
 * =========================================
 * UserService 인터페이스
 * =========================================
 * 
 * 사용자 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 */
public interface UserService {
    
    /**
     * 회원가입
     * 
     * @param request 회원가입 요청 데이터
     * @return 생성된 사용자 정보
     */
    UserResponse signup(SignupRequest request);
    
    /**
     * 로그인
     * 
     * @param request 로그인 요청 데이터
     * @return JWT 토큰이 포함된 로그인 응답
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 소셜 로그인 (카카오/구글)
     * 
     * @param socialId 소셜 제공자의 사용자 ID
     * @param email 이메일
     * @param signupType 가입 유형 (2: 카카오, 3: 구글)
     * @return 로그인 응답
     */
    LoginResponse socialLogin(String socialId, String email, Integer signupType);
    
    /**
     * 토큰 갱신
     * 
     * @param refreshToken Refresh Token
     * @return 새로운 토큰 정보
     */
    LoginResponse refreshToken(String refreshToken);
    
    /**
     * 로그아웃
     * 
     * @param userId 사용자 ID
     */
    void logout(Long userId);
    
    /**
     * 사용자 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    UserResponse getUserById(Long userId);
    
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 이메일
     * @return 사용자 엔티티 (Optional)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    boolean isEmailDuplicate(String email);
    
    /**
     * 연락처로 이메일 찾기
     * 
     * @param phone 연락처
     * @return 마스킹된 이메일 (예: t***@gmail.com)
     */
    String findEmailByPhone(String phone);
    
    /**
     * 사용자 정보 수정
     * 
     * @param userId 사용자 ID
     * @param userName 변경할 반려동물 이름
     * @param phone 변경할 연락처
     * @return 수정된 사용자 정보
     */
    UserResponse updateUser(Long userId, String userName, String phone);
    
    /**
     * 비밀번호 변경
     * 
     * @param userId 사용자 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     */
    void changePassword(Long userId, String currentPassword, String newPassword);
    
    /**
     * 알림 설정 변경
     * 
     * @param userId 사용자 ID
     * @param scheduleSet 알림 설정 (1: 켜기, 0: 끄기)
     */
    void updateScheduleSet(Long userId, Integer scheduleSet);
    
    /**
     * 회원 탈퇴
     * 
     * @param userId 사용자 ID
     * @param password 비밀번호 확인
     */
    void deleteUser(Long userId, String password);
}
