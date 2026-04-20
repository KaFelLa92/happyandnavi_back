# 해피야나비야 백엔드 (HappyAndNavi Backend)

반려동물 일상 기록 및 추억 공유 앱의 백엔드 서버입니다.

## 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Gradle 8.5
- **Database**: MySQL 8.0
- **ORM**: MyBatis 3.0
- **Authentication**: JWT (JJWT 0.12.3)
- **Security**: Spring Security 6

## 프로젝트 구조

```
src/main/java/com/happyandnavi/
├── HappyAndNaviApplication.java    # 메인 애플리케이션
├── config/                          # 설정 클래스
├── controller/                      # REST API 컨트롤러
├── service/                         # 비즈니스 로직
├── mapper/                          # MyBatis Mapper
├── domain/                          # 도메인 클래스
├── dto/                             # DTO 클래스
├── exception/                       # 예외 처리
└── util/                            # 유틸리티
```

## 시작하기

### 사전 요구사항

- JDK 17 이상
- MySQL 8.0 이상
- Gradle 8.x (또는 프로젝트 내 Gradle Wrapper 사용)

### 데이터베이스 설정

1. MySQL에서 데이터베이스 생성:
```sql
CREATE DATABASE happyandnavi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. `application.properties`에서 DB 연결 정보 수정

### 실행 방법

```bash
# Gradle Wrapper로 실행
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/happyandnavi_back-0.0.1-SNAPSHOT.jar
```

## API 엔드포인트

### 인증 API (`/api/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /signup | 회원가입 |
| POST | /login | 로그인 |
| POST | /refresh | 토큰 갱신 |
| POST | /logout | 로그아웃 |
| GET | /check-email | 이메일 중복 확인 |
| POST | /find-email | 이메일 찾기 |

### 사용자 API (`/api/users`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /me | 내 정보 조회 |
| PUT | /me | 내 정보 수정 |
| PUT | /me/password | 비밀번호 변경 |
| PUT | /me/settings | 알림 설정 변경 |
| DELETE | /me | 회원 탈퇴 |

### 추억일기 API (`/api/memories`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | / | 추억일기 등록 |
| GET | / | 전체 조회 (페이징) |
| GET | /{id} | 상세 조회 |
| GET | /calendar | 월별 캘린더 조회 |
| GET | /year/{year} | 연도별 조회 |
| GET | /search | 검색 |
| PUT | /{id} | 수정 |
| DELETE | /{id} | 삭제 |

### 일정 API (`/api/promises`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | / | 일정 등록 |
| GET | / | 전체 조회 |
| GET | /{id} | 상세 조회 |
| GET | /calendar | 월별 캘린더 조회 |
| GET | /today | 오늘 일정 |
| GET | /upcoming | 다가오는 일정 |
| GET | /search | 검색 |
| PUT | /{id} | 수정 |
| DELETE | /{id} | 삭제 |

## 인증 방식

JWT(JSON Web Token) 기반 인증을 사용합니다.

```
Authorization: Bearer {accessToken}
```

## 환경 설정

`application.properties` 주요 설정:

```properties
# 서버 포트
server.port=8080

# 데이터베이스
spring.datasource.url=jdbc:mysql://localhost:3306/happyandnavi
spring.datasource.username=root
spring.datasource.password=1234

# JWT
jwt.secret=your-secret-key
jwt.access-token-validity=3600000
jwt.refresh-token-validity=604800000

# 파일 업로드
file.upload.base-path=C:/upload/happyandnavi
```
