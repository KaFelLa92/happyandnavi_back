package com.happyandnavi.config.jwt;

import com.happyandnavi.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * =========================================
 * JwtAuthenticationFilter 클래스
 * =========================================
 * 
 * 모든 HTTP 요청에 대해 JWT 토큰을 검증하는 필터입니다.
 * 
 * OncePerRequestFilter를 상속하여 요청당 한 번만 실행되도록 합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Authorization 헤더 이름
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * Bearer 토큰 접두사
     */
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * 필터 실행 메서드
     * 
     * 1. Authorization 헤더에서 JWT 토큰 추출
     * 2. 토큰 유효성 검증
     * 3. 인증 정보를 SecurityContext에 저장
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. 토큰 추출
            String token = resolveToken(request);
            
            // 2. 토큰 유효성 검증
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 3. 토큰에서 사용자 정보 추출
                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                String email = jwtTokenProvider.getEmailFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);
                
                // 4. 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userId,                                          // principal: 사용자 ID
                                null,                                            // credentials: 비밀번호 (null)
                                Collections.singletonList(new SimpleGrantedAuthority(role))  // 권한
                        );
                
                // 5. SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("JWT 인증 성공: userId={}, email={}", userId, email);
            }
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
            // 예외가 발생해도 필터 체인 계속 진행 (인증 없이 요청 처리)
            // 인증이 필요한 엔드포인트는 SecurityConfig에서 차단됨
        }
        
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
    
    /**
     * HTTP 요청에서 JWT 토큰 추출
     * 
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 (없으면 null)
     */
    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더 값 가져오기
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        // "Bearer " 접두사가 있는 경우 토큰 부분만 추출
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
}
