package com.happyandnavi.mapper;

import com.happyandnavi.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * =========================================
 * UserMapper 인터페이스
 * =========================================
 * 
 * 사용자(User) 테이블에 대한 데이터 액세스를 담당하는 MyBatis Mapper입니다.
 * 
 * @Mapper 어노테이션은 이 인터페이스가 MyBatis Mapper임을 나타냅니다.
 * Spring Boot가 자동으로 이 인터페이스의 구현체를 생성합니다.
 * 
 * XML 매퍼 파일 위치: resources/mapper/UserMapper.xml
 */
@Mapper
public interface UserMapper {
    
    // ========================================
    // CREATE (등록)
    // ========================================
    
    /**
     * 새로운 사용자를 등록합니다.
     * 
     * @param user 등록할 사용자 정보
     * @return 영향받은 행 수 (성공 시 1)
     */
    int insert(User user);
    
    // ========================================
    // READ (조회)
    // ========================================
    
    /**
     * 사용자 ID로 사용자를 조회합니다.
     * 
     * @param userId 사용자 고유 번호
     * @return 사용자 정보 (Optional로 래핑하여 null 안전성 확보)
     */
    Optional<User> findById(@Param("userId") Long userId);
    
    /**
     * 이메일로 사용자를 조회합니다.
     * 로그인 및 중복 확인에 사용됩니다.
     * 
     * @param email 이메일 주소
     * @return 사용자 정보
     */
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * 소셜 로그인 제공자 ID로 사용자를 조회합니다.
     * 카카오/구글 로그인 시 기존 가입 여부 확인에 사용됩니다.
     * 
     * @param socialId 소셜 제공자에서 발급한 고유 ID
     * @param signupType 가입 방법 (2: 카카오, 3: 구글)
     * @return 사용자 정보
     */
    Optional<User> findBySocialIdAndSignupType(
            @Param("socialId") String socialId,
            @Param("signupType") Integer signupType
    );
    
    /**
     * 연락처로 사용자를 조회합니다.
     * 이메일 찾기 기능에 사용됩니다.
     * 
     * @param phone 연락처
     * @return 사용자 정보
     */
    Optional<User> findByPhone(@Param("phone") String phone);
    
    /**
     * 이메일 중복 확인
     * 
     * @param email 확인할 이메일
     * @return 존재 여부 (1: 존재, 0: 미존재)
     */
    int existsByEmail(@Param("email") String email);
    
    /**
     * 연락처 중복 확인
     * 
     * @param phone 확인할 연락처
     * @return 존재 여부 (1: 존재, 0: 미존재)
     */
    int existsByPhone(@Param("phone") String phone);
    
    // ========================================
    // UPDATE (수정)
    // ========================================
    
    /**
     * 사용자 정보를 수정합니다.
     * 
     * @param user 수정할 사용자 정보
     * @return 영향받은 행 수
     */
    int update(User user);
    
    /**
     * 비밀번호를 변경합니다.
     * 
     * @param userId 사용자 ID
     * @param password 새 비밀번호 (암호화된 상태)
     * @return 영향받은 행 수
     */
    int updatePassword(
            @Param("userId") Long userId,
            @Param("password") String password
    );
    
    /**
     * Refresh Token을 업데이트합니다.
     * 로그인 시 새 토큰 저장, 로그아웃 시 null로 설정
     * 
     * @param userId 사용자 ID
     * @param refreshToken 새 Refresh Token (로그아웃 시 null)
     * @return 영향받은 행 수
     */
    int updateRefreshToken(
            @Param("userId") Long userId,
            @Param("refreshToken") String refreshToken
    );
    
    /**
     * 일정 알림 설정을 변경합니다.
     * 
     * @param userId 사용자 ID
     * @param scheduleSet 알림 설정 (1: 켜짐, 0: 꺼짐)
     * @return 영향받은 행 수
     */
    int updateScheduleSet(
            @Param("userId") Long userId,
            @Param("scheduleSet") Integer scheduleSet
    );
    
    // ========================================
    // DELETE (삭제)
    // ========================================
    
    /**
     * 사용자를 논리적으로 삭제합니다. (status를 0으로 변경)
     * 실제 데이터는 보존되며, 상태만 비활성화됩니다.
     * 
     * @param userId 사용자 ID
     * @return 영향받은 행 수
     */
    int deleteById(@Param("userId") Long userId);
    
    /**
     * 사용자를 물리적으로 삭제합니다.
     * 주의: 이 작업은 되돌릴 수 없으며, 관련 데이터도 함께 삭제될 수 있습니다.
     * 
     * @param userId 사용자 ID
     * @return 영향받은 행 수
     */
    int hardDeleteById(@Param("userId") Long userId);
}
