package com.happyandnavi.controller;

import com.happyandnavi.dto.common.ApiResponse;
import com.happyandnavi.dto.user.UserResponse;
import com.happyandnavi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * =========================================
 * UserController 클래스
 * =========================================
 * 
 * 사용자 관련 API 엔드포인트를 제공하는 컨트롤러입니다.
 * 
 * 제공하는 API:
 * - GET /api/users/me : 내 정보 조회
 * - PUT /api/users/me : 내 정보 수정
 * - PUT /api/users/me/password : 비밀번호 변경
 * - PUT /api/users/me/settings : 알림 설정 변경
 * - DELETE /api/users/me : 회원 탈퇴
 * 
 * 모든 API는 JWT 인증이 필요합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 내 정보 조회 API
     * 
     * @param userId 인증된 사용자 ID (JWT에서 추출)
     * @return 사용자 정보
     * 
     * @AuthenticationPrincipal: SecurityContext에서 인증된 사용자 정보 추출
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(@AuthenticationPrincipal Long userId) {
        log.info("내 정보 조회: userId={}", userId);
        
        UserResponse response = userService.getUserById(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 내 정보 수정 API
     * 
     * @param userId 인증된 사용자 ID
     * @param request 수정 요청 데이터
     * @return 수정된 사용자 정보
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyInfo(
            @AuthenticationPrincipal Long userId,
            @RequestBody UpdateUserRequest request) {
        log.info("내 정보 수정: userId={}", userId);
        
        UserResponse response = userService.updateUser(userId, request.getPetName(), request.getPhone());
        
        return ResponseEntity.ok(ApiResponse.success(response, "정보가 수정되었습니다."));
    }

    /**
     * 반려동물 프로필 사진 업로드 API
     */
    @PutMapping("/me/pet-photo")
    public ResponseEntity<ApiResponse<UserResponse>> uploadPetPhoto(
            @AuthenticationPrincipal Long userId,
            @RequestParam("image") MultipartFile image) {
        log.info("반려동물 사진 업로드 요청: userId={}", userId);

        UserResponse response = userService.uploadPetPhoto(userId, image);

        return ResponseEntity.ok(ApiResponse.success(response, "사진이 업데이트 되었습니다."));
    }
    
    /**
     * 비밀번호 변경 API
     * 
     * @param userId 인증된 사용자 ID
     * @param request 비밀번호 변경 요청 데이터
     * @return 성공 메시지
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal Long userId,
            @RequestBody ChangePasswordRequest request) {
        log.info("비밀번호 변경: userId={}", userId);
        
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다."));
    }
    
    /**
     * 알림 설정 변경 API
     * 
     * @param userId 인증된 사용자 ID
     * @param request 설정 변경 요청 데이터
     * @return 성공 메시지
     */
    @PutMapping("/me/settings")
    public ResponseEntity<ApiResponse<Void>> updateSettings(
            @AuthenticationPrincipal Long userId,
            @RequestBody UpdateSettingsRequest request) {
        log.info("설정 변경: userId={}, scheduleSet={}", userId, request.getScheduleSet());
        
        userService.updateScheduleSet(userId, request.getScheduleSet());
        
        return ResponseEntity.ok(ApiResponse.success("설정이 변경되었습니다."));
    }
    
    /**
     * 회원 탈퇴 API
     * 
     * @param userId 인증된 사용자 ID
     * @param request 탈퇴 요청 데이터 (비밀번호 확인)
     * @return 성공 메시지
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal Long userId,
            @RequestBody DeleteAccountRequest request) {
        log.info("회원 탈퇴: userId={}", userId);
        
        userService.deleteUser(userId, request.getPassword());
        
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }
    
    // ========================================
    // 내부 DTO 클래스
    // ========================================
    
    @lombok.Getter
    @lombok.Setter
    public static class UpdateUserRequest {
        private String petName;
        private String phone;
    }
    
    @lombok.Getter
    @lombok.Setter
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }
    
    @lombok.Getter
    @lombok.Setter
    public static class UpdateSettingsRequest {
        private Integer scheduleSet;
    }
    
    @lombok.Getter
    @lombok.Setter
    public static class DeleteAccountRequest {
        private String password;
    }
}
