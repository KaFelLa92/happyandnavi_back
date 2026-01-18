package com.happyandnavi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * =========================================
 * WebMvcConfig 클래스
 * =========================================
 * 
 * Spring MVC 관련 설정을 담당하는 구성 클래스입니다.
 * 정적 리소스 매핑, 인터셉터 등을 설정합니다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * 파일 업로드 기본 경로
     */
    @Value("${file.upload.base-path}")
    private String uploadBasePath;
    
    /**
     * 정적 리소스 핸들러 설정
     * 
     * 업로드된 파일을 /uploads/** URL로 접근할 수 있도록 매핑합니다.
     * 예: /uploads/memory/1/2024/01/image.jpg -> C:/upload/happyandnavi/memory/1/2024/01/image.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드 파일 경로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadBasePath + "/")
                .setCachePeriod(3600);  // 캐시 시간: 1시간
    }
}
