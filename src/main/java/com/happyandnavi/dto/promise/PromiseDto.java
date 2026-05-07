package com.happyandnavi.dto.promise;

import com.happyandnavi.domain.Promise;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * =========================================
 * 약속일기(일정) 관련 DTO 클래스 모음
 * =========================================
 * 
 * 일정 CRUD 작업에 필요한 요청/응답 DTO를 정의합니다.
 */
public class PromiseDto {
    
    /**
     * =========================================
     * 일정 등록 요청 DTO
     * =========================================
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        
        /**
         * 일정 제목
         * @NotBlank: 필수 입력
         * @Size: 최대 100자
         */
        @NotBlank(message = "일정 제목은 필수 입력 항목입니다.")
        @Size(max = 100, message = "일정 제목은 100자를 초과할 수 없습니다.")
        private String promiseTitle;
        
        /**
         * 일정 아이콘 경로 (선택)
         */
        private String promiseIconPath;
        
        /**
         * 일정 색상 (선택, 기본값: yellow)
         */
        private String promiseColor;

        /**
         * 일정 카테고리 (Category enum name 저장)
         */
        private String promiseCategory;
        
        /**
         * 일정 메모/코멘트 (선택)
         */
        @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다.")
        private String promiseComment;
        
        /**
         * 일정 시작 시간
         */
        private LocalDateTime promiseStart;
        
        /**
         * 일정 종료 시간
         */
        private LocalDateTime promiseEnd;
        
        /**
         * 알림 설정 (분 단위)
         * 예: 30 = 30분 전 알림
         */
        @Min(value = 0, message = "알림 시간은 0 이상이어야 합니다.")
        private Integer reminderMinutes;
        
        /**
         * 반복 유형
         * 0: 반복 없음, 1: 매일, 2: 매주, 3: 매월, 4: 매년
         */
        @Min(value = 0, message = "올바른 반복 유형이 아닙니다.")
        @Max(value = 4, message = "올바른 반복 유형이 아닙니다.")
        private Integer repeatType;
        
        /**
         * 종일 일정 여부
         */
        private Boolean allDay;
        
        /**
         * DTO를 Promise 엔티티로 변환
         * 
         * @param userId 사용자 ID
         * @return Promise 엔티티
         */
        public Promise toEntity(Long userId) {
            return Promise.builder()
                    .promiseTitle(this.promiseTitle)
                    .promiseIconPath(this.promiseIconPath)
                    .promiseColor(this.promiseColor != null ? this.promiseColor : "yellow")
                    .promiseCategory(this.promiseCategory)
                    .promiseComment(this.promiseComment)
                    .promiseStart(this.promiseStart)
                    .promiseEnd(this.promiseEnd)
                    .reminderMinutes(this.reminderMinutes)
                    .repeatType(this.repeatType != null ? this.repeatType : 0)
                    .allDay(this.allDay != null ? this.allDay : false)
                    .userId(userId)
                    .build();
        }
    }
    
    /**
     * =========================================
     * 일정 수정 요청 DTO
     * =========================================
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        
        /**
         * 일정 제목
         */
        @Size(max = 100, message = "일정 제목은 100자를 초과할 수 없습니다.")
        private String promiseTitle;
        
        /**
         * 일정 아이콘 경로
         */
        private String promiseIconPath;
        
        /**
         * 일정 색상
         */
        private String promiseColor;

        /**
         * 일정 카테고리 (Promise.Category enum name)
         */
        private String promiseCategory;
        
        /**
         * 일정 메모
         */
        @Size(max = 500, message = "메모는 500자를 초과할 수 없습니다.")
        private String promiseComment;
        
        /**
         * 일정 시작 시간
         */
        private LocalDateTime promiseStart;
        
        /**
         * 일정 종료 시간
         */
        private LocalDateTime promiseEnd;
        
        /**
         * 알림 설정
         */
        @Min(value = 0, message = "알림 시간은 0 이상이어야 합니다.")
        private Integer reminderMinutes;
        
        /**
         * 반복 유형
         */
        @Min(value = 0, message = "올바른 반복 유형이 아닙니다.")
        @Max(value = 4, message = "올바른 반복 유형이 아닙니다.")
        private Integer repeatType;
        
        /**
         * 종일 일정 여부
         */
        private Boolean allDay;
    }
    
    /**
     * =========================================
     * 일정 응답 DTO
     * =========================================
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        
        /**
         * 일정 고유 번호
         */
        private Long promiseId;
        
        /**
         * 일정 제목
         */
        private String promiseTitle;
        
        /**
         * 아이콘 경로
         */
        private String promiseIconPath;
        
        /**
         * 일정 색상
         */
        private String promiseColor;

        /**
         * 일정 카테고리 enum name
         */
        private String promiseCategory;

        /**
         * 일정 카테고리 한국어 이름 (예: "예방접종")
         */
        private String categoryName;

        /**
         * 일정 카테고리 이모지 (예: "💉")
         */
        private String categoryEmoji;
        
        /**
         * 일정 메모
         */
        private String promiseComment;
        
        /**
         * 시작 시간
         */
        private LocalDateTime promiseStart;
        
        /**
         * 종료 시간
         */
        private LocalDateTime promiseEnd;
        
        /**
         * 알림 설정 (분 단위)
         */
        private Integer reminderMinutes;
        
        /**
         * 알림 설정 문자열 (예: "30분 전", "1시간 전")
         */
        private String reminderText;
        
        /**
         * 반복 유형 코드
         */
        private Integer repeatType;
        
        /**
         * 반복 유형 문자열 (예: "매일", "매주")
         */
        private String repeatTypeText;
        
        /**
         * 종일 일정 여부
         */
        private Boolean allDay;
        
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
         * Promise 엔티티를 Response DTO로 변환
         * 
         * @param promise Promise 엔티티
         * @return Response DTO
         */
        public static Response fromEntity(Promise promise) {
            // 반복 유형 문자열 변환
            String repeatTypeText = "";
            if (promise.getRepeatType() != null) {
                Promise.RepeatType type = Promise.RepeatType.fromCode(promise.getRepeatType());
                repeatTypeText = type.getDescription();
            }
            
            // 알림 설정 문자열 변환
            String reminderText = "";
            if (promise.getReminderMinutes() != null && promise.getReminderMinutes() > 0) {
                int minutes = promise.getReminderMinutes();
                if (minutes < 60) {
                    reminderText = minutes + "분 전";
                } else if (minutes < 1440) {
                    reminderText = (minutes / 60) + "시간 전";
                } else {
                    reminderText = (minutes / 1440) + "일 전";
                }
            }

            // 카테고리 정보
            String categoryName = "", categoryEmoji = "";
            if (promise.getPromiseCategory() != null) {
                Promise.Category cat = Promise.Category.fromName(promise.getPromiseCategory());
                if (cat != null) { categoryName = cat.getName(); categoryEmoji = cat.getEmoji(); }
            }
            
            return Response.builder()
                    .promiseId(promise.getPromiseId())
                    .promiseTitle(promise.getPromiseTitle())
                    .promiseIconPath(promise.getPromiseIconPath())
                    .promiseColor(promise.getPromiseColor())
                    .promiseCategory(promise.getPromiseCategory())
                    .categoryName(categoryName)
                    .categoryEmoji(categoryEmoji)
                    .promiseComment(promise.getPromiseComment())
                    .promiseStart(promise.getPromiseStart())
                    .promiseEnd(promise.getPromiseEnd())
                    .reminderMinutes(promise.getReminderMinutes())
                    .reminderText(reminderText)
                    .repeatType(promise.getRepeatType())
                    .repeatTypeText(repeatTypeText)
                    .allDay(promise.getAllDay())
                    .userId(promise.getUserId())
                    .regDate(promise.getRegDate())
                    .modDate(promise.getModDate())
                    .build();
        }
    }
    
    /**
     * =========================================
     * 캘린더용 일정 간략 DTO
     * =========================================
     * 
     * 캘린더에서 일정 목록을 표시할 때 사용하는 간략한 DTO입니다.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarItem {
        
        /**
         * 일정 고유 번호
         */
        private Long promiseId;
        
        /**
         * 일정 제목
         */
        private String promiseTitle;
        
        /**
         * 일정 색상
         */
        private String promiseColor;

        /**
         * 일정 카테고리 이모지 (캘린더 바에 표시)
         */
        private String categoryEmoji;
        
        /**
         * 시작 시간
         */
        private LocalDateTime promiseStart;
        
        /**
         * 종료 시간
         */
        private LocalDateTime promiseEnd;
        
        /**
         * 종일 일정 여부
         */
        private Boolean allDay;
        
        /**
         * Promise 엔티티를 CalendarItem으로 변환
         * 
         * @param promise Promise 엔티티
         * @return CalendarItem DTO
         */
        public static CalendarItem fromEntity(Promise promise) {
            // 카테고리 이모지 정보
            String categoryEmoji = "";
            if (promise.getPromiseCategory() != null) {
                Promise.Category cat = Promise.Category.fromName(promise.getPromiseCategory());
                if (cat != null) categoryEmoji = cat.getEmoji();
            }
            return CalendarItem.builder()
                    .promiseId(promise.getPromiseId())
                    .promiseTitle(promise.getPromiseTitle())
                    .promiseColor(promise.getPromiseColor())
                    .categoryEmoji(categoryEmoji)
                    .promiseStart(promise.getPromiseStart())
                    .promiseEnd(promise.getPromiseEnd())
                    .allDay(promise.getAllDay())
                    .build();
        }
    }
}
