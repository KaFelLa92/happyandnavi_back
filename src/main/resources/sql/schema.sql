-- ============================================
-- 해피야나비야 데이터베이스 스키마
-- ============================================
-- 
-- 이 파일은 애플리케이션 시작 시 자동으로 실행됩니다.
-- MySQL 8.0 이상 호환

-- 데이터베이스 생성 (수동 실행 필요)
-- CREATE DATABASE IF NOT EXISTS happyandnavi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE happyandnavi;

-- ============================================
-- 1. Users 테이블 (사용자)
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    -- PK: 사용자 고유 번호
    user_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    
    -- 기본 정보
    email VARCHAR(40) NOT NULL COMMENT '이메일 (로그인 ID)',
    password VARCHAR(255) COMMENT '비밀번호 (BCrypt 암호화)',
    phone VARCHAR(15) COMMENT '연락처',
    pet_name VARCHAR(30) NOT NULL COMMENT '반려동물 이름',
    pet_photo_path TEXT DEFAULT NULL COMMENT '반려동물 프로필 사진 경로',
    
    -- 설정
    schedule_set INT NOT NULL DEFAULT 1 COMMENT '일정 알림 설정 (1: 켜짐, 0: 꺼짐)',
    signup_type INT NOT NULL DEFAULT 1 COMMENT '가입 방법 (1: 일반, 2: 카카오, 3: 구글)',
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER' COMMENT '권한 (ROLE_USER, ROLE_ADMIN)',
    
    -- 소셜 로그인
    social_id VARCHAR(100) COMMENT '소셜 로그인 제공자 ID',
    refresh_token VARCHAR(500) COMMENT 'JWT Refresh Token',
    
    -- 공통 필드
    status INT NOT NULL DEFAULT 1 COMMENT '상태 (1: 활성, 0: 비활성)',
    reg_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    mod_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    
    -- 제약 조건
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email),
    INDEX idx_users_social (social_id, signup_type),
    INDEX idx_users_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 테이블';

-- ============================================
-- 2. Memory 테이블 (추억일기)
-- ============================================
CREATE TABLE IF NOT EXISTS memory (
    -- PK: 추억일기 고유 번호
    memory_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    
    -- 추억 정보
    memory_date DATE NOT NULL COMMENT '추억 날짜',
    memory_path TEXT NOT NULL COMMENT '사진 저장 경로',
    memory_comment TEXT COMMENT '코멘트 (200자 이내)',
    memory_weather INT COMMENT '날씨 (1:맑음, 2:흐림, 3:비, 4:눈, 5:바람)',
    user_mood INT COMMENT '사용자 기분 (1:매우좋음 ~ 5:매우나쁨)',
    pet_mood INT COMMENT '반려동물 기분 (1:매우좋음 ~ 5:매우나쁨)',
    
    -- FK: 사용자 번호
    user_id BIGINT UNSIGNED NOT NULL,
    
    -- 공통 필드
    status INT NOT NULL DEFAULT 1 COMMENT '상태 (1: 활성, 0: 삭제)',
    reg_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    mod_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    
    -- 제약 조건
    PRIMARY KEY (memory_id),
    INDEX idx_memory_user_date (user_id, memory_date),
    INDEX idx_memory_year_month (user_id, memory_date),
    CONSTRAINT fk_memory_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='추억일기 테이블';

-- ============================================
-- 3. Promise 테이블 (일정/약속일기)
-- ============================================
CREATE TABLE IF NOT EXISTS promise (
    -- PK: 일정 고유 번
    promise_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    
    -- 일정 정보
    promise_title VARCHAR(100) NOT NULL COMMENT '일정 제목',
    promise_icon_path TEXT COMMENT '아이콘 경로',
    promise_color VARCHAR(100) NOT NULL DEFAULT 'yellow' COMMENT '일정 색상',
    promise_category VARCHAR(30) DEFAULT NULL COMMENT '카테고리 (VACCINATION/CHECKUP/GROOMING/WALK/FOOD/SNACK/NAIL/SURGERY/OTHER)',
    promise_comment TEXT COMMENT '메모',
    promise_start DATETIME COMMENT '시작 시간',
    promise_end DATETIME COMMENT '종료 시간',
    
    -- 추가 설정
    reminder_minutes INT COMMENT '알림 설정 (분 단위)',
    repeat_type INT DEFAULT 0 COMMENT '반복 유형 (0:없음, 1:매일, 2:매주, 3:매월, 4:매년)',
    all_day BOOLEAN DEFAULT FALSE COMMENT '종일 일정 여부',
    
    -- FK: 사용자 번호
    user_id BIGINT UNSIGNED NOT NULL,
    
    -- 공통 필드
    status INT NOT NULL DEFAULT 1 COMMENT '상태 (1: 활성, 0: 삭제)',
    reg_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    mod_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    
    -- 제약 조건
    PRIMARY KEY (promise_id),
    INDEX idx_promise_user_start (user_id, promise_start),
    INDEX idx_promise_reminder (promise_start, reminder_minutes),
    CONSTRAINT fk_promise_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='일정 테이블';
