package com.happyandnavi.dto.memory;

import com.happyandnavi.domain.Memory;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * =========================================
 * 추억일기 관련 DTO 클래스 모음
 * =========================================
 * 
 * 추억일기 CRUD 작업에 필요한 요청/응답 DTO를 정의합니다.
 */
public class MemoryDto {
    
    /**
     * =========================================
     * 추억일기 등록 요청 DTO
     * =========================================
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        
        /**
         * 추억 날짜
         * @NotNull: 필수 입력
         * @PastOrPresent: 미래 날짜 불가 (과거 또는 오늘만 가능)
         */
        @NotNull(message = "추억 날짜는 필수 입력 항목입니다.")
        @PastOrPresent(message = "미래 날짜는 입력할 수 없습니다.")
        private LocalDate memoryDate;
        
        /**
         * 추억 코멘트 (선택)
         * @Size: 최대 200자 (트위터 스타일)
         */
        @Size(max = 200, message = "코멘트는 200자를 초과할 수 없습니다.")
        private String memoryComment;
        
        /**
         * 날씨 코드
         * 1: 맑음, 2: 흐림, 3: 비, 4: 눈, 5: 바람
         */
        @Min(value = 1, message = "올바른 날씨 코드가 아닙니다.")
        @Max(value = 5, message = "올바른 날씨 코드가 아닙니다.")
        private Integer memoryWeather;
        
        /**
         * 사용자(보호자) 기분 코드
         * 1: 매우 좋음, 2: 좋음, 3: 보통, 4: 나쁨, 5: 매우 나쁨
         */
        @Min(value = 1, message = "올바른 기분 코드가 아닙니다.")
        @Max(value = 5, message = "올바른 기분 코드가 아닙니다.")
        private Integer userMood;
        
        /**
         * 반려동물 기분 코드
         * 1: 매우 좋음, 2: 좋음, 3: 보통, 4: 나쁨, 5: 매우 나쁨
         */
        @Min(value = 1, message = "올바른 기분 코드가 아닙니다.")
        @Max(value = 5, message = "올바른 기분 코드가 아닙니다.")
        private Integer petMood;
        
        /**
         * DTO를 Memory 엔티티로 변환
         * 
         * @param userId 사용자 ID (인증 정보에서 추출)
         * @param imagePath 업로드된 이미지 경로
         * @return Memory 엔티티
         */
        public Memory toEntity(Long userId, String imagePath) {
            return Memory.builder()
                    .memoryDate(this.memoryDate)
                    .memoryPath(imagePath)
                    .memoryComment(this.memoryComment)
                    .memoryWeather(this.memoryWeather)
                    .userMood(this.userMood)
                    .petMood(this.petMood)
                    .userId(userId)
                    .build();
        }
    }
    
    /**
     * =========================================
     * 추억일기 수정 요청 DTO
     * =========================================
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        
        /**
         * 추억 날짜 (수정 불가능할 수도 있음 - 정책에 따라)
         */
        @PastOrPresent(message = "미래 날짜는 입력할 수 없습니다.")
        private LocalDate memoryDate;
        
        /**
         * 추억 코멘트
         */
        @Size(max = 200, message = "코멘트는 200자를 초과할 수 없습니다.")
        private String memoryComment;
        
        /**
         * 날씨 코드
         */
        @Min(value = 1, message = "올바른 날씨 코드가 아닙니다.")
        @Max(value = 5, message = "올바른 날씨 코드가 아닙니다.")
        private Integer memoryWeather;
        
        /**
         * 사용자 기분 코드
         */
        @Min(value = 1, message = "올바른 기분 코드가 아닙니다.")
        @Max(value = 5, message = "올바른 기분 코드가 아닙니다.")
        private Integer userMood;
        
        /**
         * 반려동물 기분 코드
         */
        @Min(value = 1, message = "올바른 기분 코드가 아닙니다.")
        @Max(value = 5, message = "올바른 기분 코드가 아닙니다.")
        private Integer petMood;
    }
    
    /**
     * =========================================
     * 추억일기 응답 DTO
     * =========================================
     * 
     * 추억일기 조회 시 반환하는 DTO입니다.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        
        /**
         * 추억일기 고유 번호
         */
        private Long memoryId;
        
        /**
         * 추억 날짜
         */
        private LocalDate memoryDate;
        
        /**
         * 사진 경로
         */
        private String memoryPath;
        
        /**
         * 사진 URL (프론트엔드에서 접근 가능한 URL)
         */
        private String memoryUrl;
        
        /**
         * 코멘트
         */
        private String memoryComment;
        
        /**
         * 날씨 코드
         */
        private Integer memoryWeather;
        
        /**
         * 날씨 문자열 (예: "맑음", "흐림")
         */
        private String weatherText;
        
        /**
         * 날씨 이모지 (예: "☀️", "☁️")
         */
        private String weatherEmoji;
        
        /**
         * 사용자 기분 코드
         */
        private Integer userMood;
        
        /**
         * 사용자 기분 문자열
         */
        private String userMoodText;
        
        /**
         * 사용자 기분 이모지
         */
        private String userMoodEmoji;
        
        /**
         * 반려동물 기분 코드
         */
        private Integer petMood;
        
        /**
         * 반려동물 기분 문자열
         */
        private String petMoodText;
        
        /**
         * 반려동물 기분 이모지
         */
        private String petMoodEmoji;
        
        /**
         * 사용자 ID
         */
        private Long userId;
        
        /**
         * 생성 일시
         */
        private LocalDateTime regDate;
        
        /**
         * 수정 일시
         */
        private LocalDateTime modDate;
        
        /**
         * Memory 엔티티를 Response DTO로 변환
         * 
         * @param memory Memory 엔티티
         * @param baseUrl 이미지 기본 URL
         * @return Response DTO
         */
        public static Response fromEntity(Memory memory, String baseUrl) {
            // 날씨 정보 변환
            String weatherText = "";
            String weatherEmoji = "";
            if (memory.getMemoryWeather() != null) {
                Memory.Weather weather = Memory.Weather.fromCode(memory.getMemoryWeather());
                if (weather != null) {
                    weatherText = weather.getDescription();
                    weatherEmoji = weather.getEmoji();
                }
            }
            
            // 사용자 기분 정보 변환
            String userMoodText = "";
            String userMoodEmoji = "";
            if (memory.getUserMood() != null) {
                Memory.Mood mood = Memory.Mood.fromCode(memory.getUserMood());
                if (mood != null) {
                    userMoodText = mood.getDescription();
                    userMoodEmoji = mood.getEmoji();
                }
            }
            
            // 반려동물 기분 정보 변환
            String petMoodText = "";
            String petMoodEmoji = "";
            if (memory.getPetMood() != null) {
                Memory.Mood mood = Memory.Mood.fromCode(memory.getPetMood());
                if (mood != null) {
                    petMoodText = mood.getDescription();
                    petMoodEmoji = mood.getEmoji();
                }
            }
            
            return Response.builder()
                    .memoryId(memory.getMemoryId())
                    .memoryDate(memory.getMemoryDate())
                    .memoryPath(memory.getMemoryPath())
                    .memoryUrl(baseUrl + memory.getMemoryPath())
                    .memoryComment(memory.getMemoryComment())
                    .memoryWeather(memory.getMemoryWeather())
                    .weatherText(weatherText)
                    .weatherEmoji(weatherEmoji)
                    .userMood(memory.getUserMood())
                    .userMoodText(userMoodText)
                    .userMoodEmoji(userMoodEmoji)
                    .petMood(memory.getPetMood())
                    .petMoodText(petMoodText)
                    .petMoodEmoji(petMoodEmoji)
                    .userId(memory.getUserId())
                    .regDate(memory.getRegDate())
                    .modDate(memory.getModDate())
                    .build();
        }
    }
    
    /**
     * =========================================
     * 캘린더용 간략 응답 DTO
     * =========================================
     * 
     * 월별 캘린더에서 썸네일로 표시할 때 사용하는 간략한 DTO입니다.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarItem {
        
        /**
         * 추억일기 고유 번호
         */
        private Long memoryId;
        
        /**
         * 추억 날짜
         */
        private LocalDate memoryDate;
        
        /**
         * 일자 (1~31)
         */
        private int day;
        
        /**
         * 썸네일 이미지 URL
         */
        private String thumbnailUrl;
        
        /**
         * 해당 날짜에 추억이 있는지 여부
         */
        private boolean hasMemory;
        
        /**
         * Memory 엔티티를 CalendarItem으로 변환
         * 
         * @param memory Memory 엔티티
         * @param baseUrl 이미지 기본 URL
         * @return CalendarItem DTO
         */
        public static CalendarItem fromEntity(Memory memory, String baseUrl) {
            return CalendarItem.builder()
                    .memoryId(memory.getMemoryId())
                    .memoryDate(memory.getMemoryDate())
                    .day(memory.getMemoryDate().getDayOfMonth())
                    .thumbnailUrl(baseUrl + memory.getMemoryPath())
                    .hasMemory(true)
                    .build();
        }
    }
}
