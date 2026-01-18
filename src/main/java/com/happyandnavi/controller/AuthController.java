package com.happyandnavi.controller;

import com.happyandnavi.dto.common.ApiResponse;
import com.happyandnavi.dto.user.*;
import com.happyandnavi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * =========================================
 * AuthController 클래스
 * =========================================
 * 
 * 인증 관련 API 엔드포인트를 제공하는 컨트롤러입니다.
 * 
 * 제공하는 API:
 * - POST /api/auth/signup : 회원가입
 * - POST /api/auth/login : 로그인
 * - POST /api/auth/refresh : 토큰 갱신
 * - POST /api/auth/logout : 로그아웃
 * - GET /api/auth/check-email : 이메일 중복 확인
 * - POST /api/auth/find-email : 이메일 찾기
 * 
 * @RestController: REST API 컨트롤러 (응답 자동 JSON 변환)
 * @RequestMapping: 기본 URL 경로 설정
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    /**
     * 회원가입 API
     * 
     * @param request 회원가입 요청 데이터
     * @return 생성된 사용자 정보
     * 
     * @Valid: 요청 데이터 유효성 검증 수행
     * @RequestBody: JSON 요청 본문을 객체로 변환
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest request) {
        log.info("회원가입 요청: email={}", request.getEmail());
        
        // 서비스 호출
        UserResponse response = userService.signup(request);
        
        // 201 Created 응답 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "회원가입이 완료되었습니다."));
    }
    
    /**
     * 로그인 API
     * 
     * @param request 로그인 요청 데이터 (이메일, 비밀번호)
     * @return JWT 토큰 정보
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("로그인 요청: email={}", request.getEmail());
        
        LoginResponse response = userService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "로그인에 성공했습니다."));
    }
    
    /**
     * 토큰 갱신 API
     * 
     * Refresh Token으로 새로운 Access Token을 발급받습니다.
     * 
     * @param refreshToken Refresh Token (요청 본문)
     * @return 새로운 토큰 정보
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("토큰 갱신 요청");
        
        LoginResponse response = userService.refreshToken(request.getRefreshToken());
        
        return ResponseEntity.ok(ApiResponse.success(response, "토큰이 갱신되었습니다."));
    }
    
    /**
     * 로그아웃 API
     * 
     * 서버에 저장된 Refresh Token을 삭제합니다.
     * 
     * @param userId 로그아웃할 사용자 ID
     * @return 성공 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam Long userId) {
        log.info("로그아웃 요청: userId={}", userId);
        
        userService.logout(userId);
        
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다."));
    }
    
    /**
     * 이메일 중복 확인 API
     * 
     * @param email 확인할 이메일
     * @return 중복 여부 (true: 중복, false: 사용 가능)
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailDuplicate(@RequestParam String email) {
        log.info("이메일 중복 확인: email={}", email);
        
        boolean isDuplicate = userService.isEmailDuplicate(email);
        
        String message = isDuplicate ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.";
        return ResponseEntity.ok(ApiResponse.success(isDuplicate, message));
    }
    
    /**
     * 이메일 찾기 API
     * 
     * 연락처로 가입된 이메일을 찾습니다 (마스킹 처리).
     * 
     * @param phone 연락처
     * @return 마스킹된 이메일 (예: t***@gmail.com)
     */
    @PostMapping("/find-email")
    public ResponseEntity<ApiResponse<String>> findEmail(@RequestBody FindEmailRequest request) {
        log.info("이메일 찾기 요청: phone={}", request.getPhone());
        
        String maskedEmail = userService.findEmailByPhone(request.getPhone());
        
        return ResponseEntity.ok(ApiResponse.success(maskedEmail, "이메일을 찾았습니다."));
    }
    
    // ========================================
    // 내부 DTO 클래스
    // ========================================
    
    /**
     * Refresh Token 요청 DTO
     */
    @lombok.Getter
    @lombok.Setter
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
    
    /**
     * 이메일 찾기 요청 DTO
     */
    @lombok.Getter
    @lombok.Setter
    public static class FindEmailRequest {
        private String phone;
    }
}
