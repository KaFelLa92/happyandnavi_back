package com.happyandnavi.domain;

import lombok.*;
import java.time.LocalDateTime;

/**
 * =========================================
 * Promise 도메인 클래스 (약속일기 테이블)
 * =========================================
 * 
 * 반려동물과의 미래 일정을 기록하는 스케줄 엔티티입니다.
 * 
 * 기록 가능한 일정 예시:
 * - 중성화 수술일
 * - 산책
 * - 미용실 예약
 * - 백신/예방접종
 * - 정기검진
 * - 사료 구매
 * - 간식 구매
 * - 손발톱 깎는 날
 * 
 * DB 테이블: promise
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Promise {
    
    // ========================================
    // PK 필드
    // ========================================
    
    /**
     * 약속일기 고유 번호 (Primary Key)
     * AUTO_INCREMENT로 자동 증가
     */
    private Long promiseId;
    
    // ========================================
    // 약속 정보 필드
    // ========================================
    
    /**
     * 약속(일정) 제목
     * 예: "예방접종", "미용실 예약", "산책" 등
     * NOT NULL, 최대 100자
     */
    private String promiseTitle;
    
    /**
     * 약속 아이콘 경로
     * 일정 종류별 아이콘 이미지 경로
     * NULL 허용 (기본 아이콘 사용 가능)
     * 
     * 예: /icons/vaccine.png, /icons/walk.png
     */
    private String promiseIconPath;
    
    /**
     * 약속 색상
     * 캘린더에 표시될 일정의 배경색
     * 기본값: yellow
     * 
     * 예: yellow, blue, green, red, purple 등
     */
    @Builder.Default
    private String promiseColor = "yellow";

    /**
     * 약속 카테고리 (Category enum name 저장)
     * 예: "VACCINATION", "WALK", "OTHER"
     */
    private String promiseCategory;
    
    /**
     * 약속 코멘트/메모
     * 일정에 대한 추가 설명
     * NULL 허용
     * 
     * 예: "OO동물병원에서 광견병 예방접종"
     */
    private String promiseComment;
    
    /**
     * 약속 시작 시간
     * 일정의 시작 날짜 및 시간
     * NULL 허용 (종일 일정인 경우)
     */
    private LocalDateTime promiseStart;
    
    /**
     * 약속 종료 시간
     * 일정의 종료 날짜 및 시간
     * NULL 허용 (시작 시간만 있는 경우)
     */
    private LocalDateTime promiseEnd;
    
    /**
     * 알림 설정 시간 (분 단위)
     * 일정 시작 전 몇 분 전에 알림을 보낼지 설정
     * NULL 허용
     * 
     * 예: 30 (30분 전 알림), 60 (1시간 전 알림)
     */
    private Integer reminderMinutes;
    
    /**
     * 반복 설정 (RepeatType enum과 매핑)
     * 0: 반복 없음
     * 1: 매일
     * 2: 매주
     * 3: 매월
     * 4: 매년
     */
    @Builder.Default
    private Integer repeatType = 0;
    
    /**
     * 종일 일정 여부
     * true: 종일 일정 (시간 표시 없음)
     * false: 특정 시간 일정
     */
    @Builder.Default
    private Boolean allDay = false;
    
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
     * 0: 삭제됨/완료됨
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
     * 반복 유형 열거형
     */
    public enum RepeatType {
        NONE(0, "반복 없음"),
        DAILY(1, "매일"),
        WEEKLY(2, "매주"),
        MONTHLY(3, "매월"),
        YEARLY(4, "매년");
        
        private final int code;
        private final String description;
        
        RepeatType(int code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public int getCode() { return code; }
        public String getDescription() { return description; }
        
        /**
         * 코드 값으로 RepeatType enum을 찾습니다.
         * @param code 반복 유형 코드
         * @return 해당 RepeatType enum, 없으면 NONE
         */
        public static RepeatType fromCode(int code) {
            for (RepeatType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return NONE;
        }
    }
    
    /**
     * 일정 카테고리 열거형
     * 미리 정의된 일정 종류를 제공합니다.
     */
    public enum Category {
        VACCINATION("예방접종", "💉", "blue"),
        CHECKUP("정기검진", "🏥", "green"),
        GROOMING("미용", "✂️", "pink"),
        WALK("산책", "🚶", "yellow"),
        FOOD("사료 구매", "🍚", "orange"),
        SNACK("간식 구매", "🦴", "orange"),
        NAIL("손발톱 관리", "✨", "purple"),
        SURGERY("수술", "🔬", "red"),
        OTHER("기타", "📌", "gray");

        private final String name;
        private final String emoji;
        private final String defaultColor;

        Category(String name, String emoji, String defaultColor) {
            this.name = name; this.emoji = emoji; this.defaultColor = defaultColor;
        }
        public String getName() { return name; }
        public String getEmoji() { return emoji; }
        public String getDefaultColor() { return defaultColor; }

        public static Category fromName(String name) {
            if (name == null) return null;
            try { return valueOf(name); } catch (IllegalArgumentException e) { return null; }
        }
    }
}

