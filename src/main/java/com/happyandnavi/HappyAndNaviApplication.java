package com.happyandnavi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =========================================
 * 해피야나비야 백엔드 메인 애플리케이션
 * =========================================
 * 
 * 반려동물 일상 기록 및 추억 공유 앱의 백엔드 서버입니다.
 * 
 * @SpringBootApplication 어노테이션은 다음 3가지를 포함합니다:
 * - @Configuration: 이 클래스가 설정 클래스임을 명시
 * - @EnableAutoConfiguration: Spring Boot 자동 설정 활성화
 * - @ComponentScan: 현재 패키지와 하위 패키지의 컴포넌트 자동 스캔
 * 
 * @author HappyAndNavi Team
 * @version 1.0
 */
@SpringBootApplication
public class HappyAndNaviApplication {

    /**
     * 애플리케이션 진입점 (Entry Point)
     * 
     * Spring Boot 애플리케이션을 시작합니다.
     * 내장 톰캣 서버가 자동으로 구동되며,
     * application.properties에 설정된 포트(기본 8080)에서 요청을 대기합니다.
     * 
     * @param args 커맨드라인 인자 (프로파일 지정 등에 사용 가능)
     */
    public static void main(String[] args) {
        // SpringApplication.run()은 ApplicationContext를 생성하고
        // 모든 Bean을 초기화한 후 웹 서버를 시작합니다.
        SpringApplication.run(HappyAndNaviApplication.class, args);
    }
}
