package com.happyandnavi.domain;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * =========================================
 * Memory 도메인 클래스 (추억일기 테이블)
 * =========================================
 * 
 * 반려동물과의 매일 추억을 사진과 함께 기록하는 다이어리 엔티티입니다.
 * 
 * 기능:
 * - 날짜별 사진 업로드
 * - 간단한 코멘트 작성 (200자 미만)
 * - 날씨, 사용자 기분, 반려동물 기분 기록
 * 
 * DB 테이블: memory
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Memory {
    
    // ========================================
    // PK 필드
    // ========================================
    
    /**
     * 추억일기 고유 번호 (Primary Key)
     * AUTO_INCREMENT로 자동 증가
     */
    private Long memoryId;
    
    // ========================================
    // 추억 정보 필드
    // ========================================
    
    /**
     * 추억 날짜
     * 해당 추억이 발생한 날짜 (과거 날짜 가능)
     * NOT NULL
     */
    private LocalDate memoryDate;
    
    /**
     * 추억 사진 경로
     * 업로드된 사진의 서버 저장 경로
     * NOT NULL
     * 
     * 예: /upload/happyandnavi/memory/2024/01/image_001.jpg
     */
    private String memoryPath;
    
    /**
     * 추억 코멘트
     * 간단한 설명 또는 감상 (200자 미만, 트위터 스타일)
     * NULL 허용
     */
    private String memoryComment;
    
    /**
     * 날씨 (Weather enum과 매핑)
     * 1: 맑음
     * 2: 흐림
     * 3: 비
     * 4: 눈
     * 5: 바람
     */
    private Integer memoryWeather;
    
    /**
     * 사용자(보호자) 기분 (Mood enum과 매핑)
     * 1: 매우 좋음
     * 2: 좋음
     * 3: 보통
     * 4: 나쁨
     * 5: 매우 나쁨
     */
    private Integer userMood;
    
    /**
     * 반려동물 기분 (Mood enum과 매핑)
     * 1: 매우 좋음
     * 2: 좋음
     * 3: 보통
     * 4: 나쁨
     * 5: 매우 나쁨
     */
    private Integer petMood;
    
    // ========================================
    // FK 필드
    // ========================================
    
    /**
     * 사용자 번호 (Foreign Key)
     * users 테이블의 userId 참조
     * NOT NULL
     */
    private Long userId;
    
    // ========================================
    // 공통 필드 (모든 테이블 공용)
    // ========================================
    
    /**
     * 상태 코드
     * 1: 활성 (기본값)
     * 0: 삭제됨
     */
    @Builder.Default
    private Integer status = 1;
    
    /**
     * 생성 일시
     * 레코드 생성 시 자동으로 현재 시간 저장
     */
    private LocalDateTime regDate;
    
    /**
     * 수정 일시
     * 레코드 수정 시 자동으로 현재 시간 갱신
     */
    private LocalDateTime modDate;
    
    // ========================================
    // 조인 필드 (필요 시 사용)
    // ========================================
    
    /**
     * 사용자 정보 (JOIN 시 사용)
     * 1:N 관계에서 N쪽의 사용자 정보 참조
     */
    private User user;
    
    // ========================================
    // Enum 정의
    // ========================================
    
    /**
     * 날씨 열거형
     */
    public enum Weather {
        SUNNY(1, "맑음", "☀️"),
        CLOUDY(2, "흐림", "☁️"),
        RAINY(3, "비", "🌧️"),
        SNOWY(4, "눈", "❄️"),
        WINDY(5, "바람", "💨");
        
        private final int code;
        private final String description;
        private final String emoji;
        
        Weather(int code, String description, String emoji) {
            this.code = code;
            this.description = description;
            this.emoji = emoji;
        }
        
        public int getCode() { return code; }
        public String getDescription() { return description; }
        public String getEmoji() { return emoji; }
        
        /**
         * 코드 값으로 Weather enum을 찾습니다.
         * @param code 날씨 코드
         * @return 해당 Weather enum, 없으면 null
         */
        public static Weather fromCode(int code) {
            for (Weather weather : values()) {
                if (weather.code == code) {
                    return weather;
                }
            }
            return null;
        }
    }
    
    /**
     * 기분 열거형 (사용자/반려동물 공용)
     */
    public enum Mood {
        VERY_HAPPY(1, "매우 좋음", "😄"),
        HAPPY(2, "좋음", "🙂"),
        NORMAL(3, "보통", "😐"),
        SAD(4, "나쁨", "😢"),
        VERY_SAD(5, "매우 나쁨", "😭");
        
        private final int code;
        private final String description;
        private final String emoji;
        
        Mood(int code, String description, String emoji) {
            this.code = code;
            this.description = description;
            this.emoji = emoji;
        }
        
        public int getCode() { return code; }
        public String getDescription() { return description; }
        public String getEmoji() { return emoji; }
        
        /**
         * 코드 값으로 Mood enum을 찾습니다.
         * @param code 기분 코드
         * @return 해당 Mood enum, 없으면 null
         */
        public static Mood fromCode(int code) {
            for (Mood mood : values()) {
                if (mood.code == code) {
                    return mood;
                }
            }
            return null;
        }
    }
}
