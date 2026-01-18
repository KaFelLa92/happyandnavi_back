-- ============================================
-- 해피야나비야 초기 데이터
-- ============================================
-- 
-- 개발/테스트용 샘플 데이터입니다.
-- 운영 환경에서는 이 파일을 비우거나 삭제하세요.

-- 테스트 계정은 필요시 아래 주석 해제
-- 비밀번호: test1234! (BCrypt 암호화)

INSERT IGNORE INTO users (email, password, phone, user_name, signup_type, role)
VALUES (
     'test@test.com',
     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGP9GmMCGVX0pENxVJiKN.JqzK.q',
     '01012345678',
     '멍멍이',
     1,
     'ROLE_USER'
 );
