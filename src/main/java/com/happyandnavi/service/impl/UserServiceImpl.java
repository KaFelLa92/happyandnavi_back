package com.happyandnavi.service.impl;

import com.happyandnavi.domain.User;
import com.happyandnavi.dto.user.*;
import com.happyandnavi.exception.BusinessException;
import com.happyandnavi.mapper.UserMapper;
import com.happyandnavi.service.UserService;
import com.happyandnavi.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * =========================================
 * UserServiceImpl 구현체
 * =========================================
 * 
 * UserService 인터페이스의 구현체입니다.
 * 사용자 관련 비즈니스 로직을 처리합니다.
 * 
 * @Slf4j: Lombok의 로깅 어노테이션 - log 변수 자동 생성
 * @Service: Spring의 서비스 컴포넌트로 등록
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성 (의존성 주입용)
 * @Transactional(readOnly = true): 기본적으로 읽기 전용 트랜잭션 적용
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    
    // ========================================
    // 의존성 주입 (생성자 주입 방식)
    // ========================================
    
    /**
     * 사용자 데이터 액세스 객체
     */
    private final UserMapper userMapper;
    
    /**
     * 비밀번호 암호화 인코더
     */
    private final PasswordEncoder passwordEncoder;
    
    /**
     * JWT 토큰 생성/검증 유틸리티
     */
    private final JwtTokenProvider jwtTokenProvider;
    
    // ========================================
    // 회원가입 관련 메서드
    // ========================================
    
    /**
     * 회원가입 처리
     * 
     * 1. 이메일 중복 확인
     * 2. 비밀번호 일치 확인
     * 3. 비밀번호 암호화
     * 4. 사용자 정보 저장
     */
    @Override
    @Transactional  // 쓰기 작업이므로 readOnly = false
    public UserResponse signup(SignupRequest request) {
        log.info("회원가입 시도: email={}", request.getEmail());
        
        // 1. 이메일 중복 확인
        if (userMapper.existsByEmail(request.getEmail()) > 0) {
            log.warn("회원가입 실패 - 이메일 중복: {}", request.getEmail());
            throw new BusinessException("이미 사용 중인 이메일입니다.", 400);
        }
        
        // 2. 비밀번호 일치 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.", 400);
        }
        
        // 3. 사용자 엔티티 생성 (비밀번호 암호화)
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // BCrypt 암호화
                .phone(normalizePhone(request.getPhone()))  // 전화번호 정규화
                .userName(request.getUserName())
                .signupType(1)  // 일반 회원가입
                .role("ROLE_USER")
                .build();
        
        // 4. 데이터베이스에 저장
        userMapper.insert(user);
        log.info("회원가입 성공: userId={}, email={}", user.getUserId(), user.getEmail());
        
        // 5. 응답 DTO로 변환하여 반환
        return UserResponse.fromEntity(user);
    }
    
    // ========================================
    // 로그인 관련 메서드
    // ========================================
    
    /**
     * 일반 로그인 처리
     * 
     * 1. 이메일로 사용자 조회
     * 2. 비밀번호 검증
     * 3. JWT 토큰 발급
     * 4. Refresh Token 저장
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 시도: email={}", request.getEmail());
        
        // 1. 이메일로 사용자 조회
        User user = userMapper.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 사용자 없음: {}", request.getEmail());
                    return new BusinessException("이메일 또는 비밀번호가 올바르지 않습니다.", 401);
                });
        
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: {}", request.getEmail());
            throw new BusinessException("이메일 또는 비밀번호가 올바르지 않습니다.", 401);
        }
        
        // 3. JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        
        // 4. Refresh Token 저장
        userMapper.updateRefreshToken(user.getUserId(), refreshToken);
        log.info("로그인 성공: userId={}, email={}", user.getUserId(), user.getEmail());
        
        // 5. 응답 반환
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenValidity() / 1000)  // 초 단위로 변환
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }
    
    /**
     * 소셜 로그인 처리 (카카오/구글)
     * 
     * 기존 사용자면 로그인, 없으면 자동 회원가입 후 로그인
     */
    @Override
    @Transactional
    public LoginResponse socialLogin(String socialId, String email, Integer signupType) {
        log.info("소셜 로그인 시도: socialId={}, signupType={}", socialId, signupType);
        
        // 기존 소셜 로그인 사용자 조회
        Optional<User> existingUser = userMapper.findBySocialIdAndSignupType(socialId, signupType);
        
        User user;
        if (existingUser.isPresent()) {
            // 기존 사용자 로그인
            user = existingUser.get();
            log.info("소셜 로그인 - 기존 사용자: userId={}", user.getUserId());
        } else {
            // 신규 사용자 자동 회원가입
            user = User.builder()
                    .email(email)
                    .socialId(socialId)
                    .signupType(signupType)
                    .userName("반려동물")  // 기본값, 이후 수정 가능
                    .role("ROLE_USER")
                    .build();
            
            userMapper.insert(user);
            log.info("소셜 로그인 - 신규 가입: userId={}", user.getUserId());
        }
        
        // JWT 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        
        // Refresh Token 저장
        userMapper.updateRefreshToken(user.getUserId(), refreshToken);
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenValidity() / 1000)
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }
    
    /**
     * Access Token 갱신
     */
    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        log.info("토큰 갱신 요청");
        
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("유효하지 않은 Refresh Token입니다.", 401);
        }
        
        // 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        
        // 사용자 조회 및 저장된 Refresh Token 확인
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));
        
        // 저장된 Refresh Token과 일치하는지 확인
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new BusinessException("Refresh Token이 일치하지 않습니다.", 401);
        }
        
        // 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());
        
        // 새 Refresh Token 저장
        userMapper.updateRefreshToken(user.getUserId(), newRefreshToken);
        log.info("토큰 갱신 성공: userId={}", userId);
        
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenValidity() / 1000)
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .build();
    }
    
    /**
     * 로그아웃 처리
     */
    @Override
    @Transactional
    public void logout(Long userId) {
        log.info("로그아웃: userId={}", userId);
        // Refresh Token 삭제
        userMapper.updateRefreshToken(userId, null);
    }
    
    // ========================================
    // 조회 관련 메서드
    // ========================================
    
    @Override
    public UserResponse getUserById(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));
        return UserResponse.fromEntity(user);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    @Override
    public boolean isEmailDuplicate(String email) {
        return userMapper.existsByEmail(email) > 0;
    }
    
    /**
     * 연락처로 이메일 찾기 (마스킹 처리)
     */
    @Override
    public String findEmailByPhone(String phone) {
        User user = userMapper.findByPhone(normalizePhone(phone))
                .orElseThrow(() -> new BusinessException("해당 연락처로 가입된 계정이 없습니다.", 404));
        
        // 이메일 마스킹 처리 (예: test@gmail.com -> t***@gmail.com)
        return maskEmail(user.getEmail());
    }
    
    // ========================================
    // 수정 관련 메서드
    // ========================================
    
    @Override
    @Transactional
    public UserResponse updateUser(Long userId, String userName, String phone) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));
        
        // 변경할 정보 설정
        user.setUserName(userName);
        user.setPhone(normalizePhone(phone));
        
        userMapper.update(user);
        log.info("사용자 정보 수정: userId={}", userId);
        
        return UserResponse.fromEntity(user);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));
        
        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("현재 비밀번호가 올바르지 않습니다.", 400);
        }
        
        // 새 비밀번호 암호화 후 저장
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
        log.info("비밀번호 변경: userId={}", userId);
    }
    
    @Override
    @Transactional
    public void updateScheduleSet(Long userId, Integer scheduleSet) {
        userMapper.updateScheduleSet(userId, scheduleSet);
        log.info("알림 설정 변경: userId={}, scheduleSet={}", userId, scheduleSet);
    }
    
    // ========================================
    // 삭제 관련 메서드
    // ========================================
    
    @Override
    @Transactional
    public void deleteUser(Long userId, String password) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));
        
        // 일반 회원의 경우 비밀번호 확인 (소셜 로그인은 비밀번호 없음)
        if (user.getSignupType() == 1) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BusinessException("비밀번호가 올바르지 않습니다.", 400);
            }
        }
        
        // 논리적 삭제 (status = 0)
        userMapper.deleteById(userId);
        log.info("회원 탈퇴: userId={}", userId);
    }
    
    // ========================================
    // 유틸리티 메서드
    // ========================================
    
    /**
     * 전화번호 정규화 (하이픈 제거)
     * 예: 010-1234-5678 -> 01012345678
     */
    private String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("-", "");
    }
    
    /**
     * 이메일 마스킹
     * 예: test@gmail.com -> t***@gmail.com
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 1) {
            return email;
        }
        
        // 첫 글자 + *** + @도메인
        return localPart.charAt(0) + "***@" + domain;
    }
}
