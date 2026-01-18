package com.happyandnavi.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * =========================================
 * JwtTokenProvider 클래스
 * =========================================
 * 
 * JWT(JSON Web Token) 생성, 검증, 파싱을 담당하는 유틸리티 클래스입니다.
 * 
 * JWT 구조:
 * - Header: 토큰 타입(JWT)과 알고리즘(HS256)
 * - Payload: 클레임(사용자 정보, 만료 시간 등)
 * - Signature: 서명(비밀키로 암호화)
 */
@Slf4j
@Component
public class JwtTokenProvider {
    
    /**
     * JWT 서명에 사용할 비밀키 (application.properties에서 주입)
     */
    @Value("${jwt.secret}")
    private String secretString;
    
    /**
     * Access Token 유효 시간 (밀리초)
     */
    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;
    
    /**
     * Refresh Token 유효 시간 (밀리초)
     */
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;
    
    /**
     * HMAC-SHA256 알고리즘에 사용할 SecretKey 객체
     */
    private SecretKey secretKey;
    
    /**
     * 빈 초기화 후 SecretKey 생성
     * 
     * @PostConstruct: 의존성 주입 완료 후 실행
     */
    @PostConstruct
    protected void init() {
        // 문자열 비밀키를 SecretKey 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
        log.info("JWT SecretKey 초기화 완료");
    }
    
    /**
     * Access Token 유효 시간 반환 (외부에서 사용)
     */
    public long getAccessTokenValidity() {
        return accessTokenValidity;
    }
    
    /**
     * Access Token 생성
     * 
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param role 사용자 권한
     * @return 생성된 JWT Access Token
     */
    public String createAccessToken(Long userId, String email, String role) {
        // 현재 시간과 만료 시간 계산
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidity);
        
        // JWT 빌더를 사용하여 토큰 생성
        return Jwts.builder()
                .subject(String.valueOf(userId))      // sub: 사용자 식별자
                .claim("email", email)                // 커스텀 클레임: 이메일
                .claim("role", role)                  // 커스텀 클레임: 권한
                .claim("type", "access")              // 토큰 타입 구분
                .issuedAt(now)                        // iat: 토큰 발급 시간
                .expiration(expiration)               // exp: 토큰 만료 시간
                .signWith(secretKey)                  // 서명
                .compact();                           // 토큰 문자열 생성
    }
    
    /**
     * Refresh Token 생성
     * 
     * @param userId 사용자 ID
     * @return 생성된 JWT Refresh Token
     */
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenValidity);
        
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")             // 토큰 타입 구분
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
    
    /**
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * 토큰에서 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 이메일
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }
    
    /**
     * 토큰에서 권한 추출
     * 
     * @param token JWT 토큰
     * @return 권한
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }
    
    /**
     * 토큰 유효성 검증
     * 
     * @param token 검증할 JWT 토큰
     * @return 유효 여부 (true: 유효, false: 무효)
     */
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 시도 (실패하면 예외 발생)
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰입니다.");
        } catch (SecurityException e) {
            log.warn("JWT 서명이 유효하지 않습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다.");
        }
        return false;
    }
    
    /**
     * 토큰 만료 여부 확인
     * 
     * @param token JWT 토큰
     * @return 만료 여부 (true: 만료됨, false: 유효함)
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
    
    /**
     * 토큰에서 Claims 추출
     * 
     * @param token JWT 토큰
     * @return Claims 객체
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
