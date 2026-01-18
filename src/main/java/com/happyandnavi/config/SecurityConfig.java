package com.happyandnavi.config;

import com.happyandnavi.config.jwt.JwtAuthenticationFilter;
import com.happyandnavi.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * =========================================
 * SecurityConfig 클래스
 * =========================================
 * 
 * Spring Security 설정을 담당하는 구성 클래스입니다.
 * JWT 기반 인증, CORS 설정, 비밀번호 암호화 등을 구성합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * 인증이 필요 없는 공개 엔드포인트 목록
     */
    private static final String[] PUBLIC_ENDPOINTS = {
            // 인증 관련
            "/api/auth/**",
            // Swagger UI (개발용)
            "/swagger-ui/**",
            "/v3/api-docs/**",
            // 정적 리소스
            "/uploads/**",
            "/static/**",
            // 헬스체크
            "/actuator/health",
            // 에러 페이지
            "/error"
    };
    
    /**
     * Security Filter Chain 설정
     * 
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (REST API에서는 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 세션 사용 안 함 (JWT 사용)
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 공개 엔드포인트는 인증 없이 접근 허용
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                
                // JWT 인증 필터 추가
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );
        
        return http.build();
    }
    
    /**
     * 비밀번호 암호화 인코더
     * BCrypt 알고리즘 사용 (강력한 단방향 해시)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * CORS(Cross-Origin Resource Sharing) 설정
     * React Native 앱에서의 API 호출을 허용합니다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 Origin (개발 시 모든 origin 허용, 운영 시 특정 도메인으로 제한)
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));
        
        // 노출할 응답 헤더
        configuration.setExposedHeaders(List.of("Authorization"));
        
        // 인증 정보 포함 허용
        configuration.setAllowCredentials(true);
        
        // preflight 캐시 시간 (1시간)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
