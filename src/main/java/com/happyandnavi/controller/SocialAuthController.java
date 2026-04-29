package com.happyandnavi.controller;

import com.happyandnavi.dto.common.ApiResponse;
import com.happyandnavi.dto.user.LoginResponse;
import com.happyandnavi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * =========================================
 * thController 클래스
 * =========================================
 * 
 * 소셜 로그인 (카카오, 구글) API 엔드포인트를 제공합니다.
 * 
 * 제공하는 API:
 * - POST /api/auth/kakao : 카카오 로그인
 * - POST /api/auth/google : 구글 로그인
 * 
 * 소셜 로그인 흐름:
 * 1. 클라이언트(앱)에서 소셜 SDK로 로그인하여 토큰 획득
 * 2. 획득한 토큰을 서버로 전송
 * 3. 서버에서 토큰을 검증하고 사용자 정보 조회
 * 4. 기존 사용자면 로그인, 신규면 자동 회원가입 후 로그인
 * 5. JWT 토큰 발급하여 응답
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SocialAuthController {
    
    private final UserService userService;
    
    /**
     * 카카오 로그인 API
     * 
     * 카카오 SDK에서 발급받은 Access Token으로 로그인합니다.
     * 서버에서 카카오 API를 호출하여 사용자 정보를 조회합니다.
     * 
     * @param request 카카오 Access Token
     * @return JWT 토큰 정보
     */
    @PostMapping("/kakao")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(
            @RequestBody KakaoLoginRequest request) {
        log.info("카카오 로그인 요청");
        
        try {
            // 카카오 API로 사용자 정보 조회
            KakaoUserInfo userInfo = getKakaoUserInfo(request.getAccessToken());
            
            if (userInfo == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.badRequest("카카오 사용자 정보를 가져올 수 없습니다."));
            }
            
            // 소셜 로그인 처리 (기존 사용자 로그인 또는 자동 회원가입)
            LoginResponse response = userService.socialLogin(
                    userInfo.getId(),
                    userInfo.getEmail(),
                    2  // signupType 2: 카카오
            );
            
            return ResponseEntity.ok(ApiResponse.success(response, "카카오 로그인에 성공했습니다."));
        } catch (Exception e) {
            log.error("카카오 로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("카카오 로그인에 실패했습니다."));
        }
    }
    
    /**
     * 구글 로그인 API
     * 
     * 구글 SDK에서 발급받은 ID Token으로 로그인합니다.
     * 서버에서 ID Token을 검증하고 사용자 정보를 추출합니다.
     * 
     * @param request 구글 ID Token
     * @return JWT 토큰 정보
     */
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<LoginResponse>> googleLogin(
            @RequestBody GoogleLoginRequest request) {
        log.info("구글 로그인 요청");
        
        try {
            // 구글 ID Token 검증 및 사용자 정보 추출
            GoogleUserInfo userInfo = verifyGoogleIdToken(request.getIdToken());
            
            if (userInfo == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.badRequest("구글 사용자 정보를 가져올 수 없습니다."));
            }
            
            // 소셜 로그인 처리 (기존 사용자 로그인 또는 자동 회원가입)
            LoginResponse response = userService.socialLogin(
                    userInfo.getSub(),  // Google의 고유 사용자 ID
                    userInfo.getEmail(),
                    3  // signupType 3: 구글
            );
            
            return ResponseEntity.ok(ApiResponse.success(response, "구글 로그인에 성공했습니다."));
        } catch (Exception e) {
            log.error("구글 로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("구글 로그인에 실패했습니다."));
        }
    }
    
    // ========================================
    // 카카오 API 호출 (실제 구현 필요)
    // ========================================
    
    /**
     * 카카오 API를 호출하여 사용자 정보를 조회합니다.
     * 
     * 실제 구현에서는 RestTemplate 또는 WebClient를 사용하여
     * https://kapi.kakao.com/v2/user/me 를 호출합니다.
     * 
     * @param accessToken 카카오 Access Token
     * @return 카카오 사용자 정보
     */
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        // TODO: 실제 카카오 API 호출 구현
        // 
        // RestTemplate restTemplate = new RestTemplate();
        // 
        // HttpHeaders headers = new HttpHeaders();
        // headers.setBearerAuth(accessToken);
        // headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 
        // HttpEntity<String> entity = new HttpEntity<>(headers);
        // 
        // ResponseEntity<Map> response = restTemplate.exchange(
        //     "https://kapi.kakao.com/v2/user/me",
        //     HttpMethod.GET,
        //     entity,
        //     Map.class
        // );
        // 
        // Map<String, Object> body = response.getBody();
        // String id = String.valueOf(body.get("id"));
        // Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        // String email = (String) kakaoAccount.get("email");
        // 
        // return new KakaoUserInfo(id, email);
        
        log.warn("카카오 API 호출 미구현 - 실제 구현이 필요합니다.");
        return null;
    }
    
    // ========================================
    // 구글 ID Token 검증 (실제 구현 필요)
    // ========================================
    
    /**
     * 구글 ID Token을 검증하고 사용자 정보를 추출합니다.
     * 
     * 실제 구현에서는 Google API Client Library를 사용하거나
     * 직접 JWT를 검증합니다.
     * 
     * @param idToken 구글 ID Token
     * @return 구글 사용자 정보
     */
    private GoogleUserInfo verifyGoogleIdToken(String idToken) {
        // TODO: 실제 구글 ID Token 검증 구현
        //
        // 방법 1: Google API Client Library 사용
        // GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        //     .setAudience(Collections.singletonList(CLIENT_ID))
        //     .build();
        // 
        // GoogleIdToken googleIdToken = verifier.verify(idToken);
        // if (googleIdToken != null) {
        //     Payload payload = googleIdToken.getPayload();
        //     String sub = payload.getSubject();
        //     String email = payload.getEmail();
        //     return new GoogleUserInfo(sub, email);
        // }
        //
        // 방법 2: JWT 직접 파싱 (공개키로 검증)
        // https://www.googleapis.com/oauth2/v3/certs 에서 공개키 획득
        
        log.warn("구글 ID Token 검증 미구현 - 실제 구현이 필요합니다.");
        return null;
    }
    
    // ========================================
    // 요청/응답 DTO
    // ========================================
    
    @lombok.Getter
    @lombok.Setter
    public static class KakaoLoginRequest {
        private String accessToken;
    }
    
    @lombok.Getter
    @lombok.Setter
    public static class GoogleLoginRequest {
        private String idToken;
    }
    
    @lombok.Getter
    @lombok.Setter
    @lombok.AllArgsConstructor
    private static class KakaoUserInfo {
        private String id;
        private String email;
    }
    
    @lombok.Getter
    @lombok.Setter
    @lombok.AllArgsConstructor
    private static class GoogleUserInfo {
        private String sub;  // Google의 고유 사용자 ID
        private String email;
    }
}
