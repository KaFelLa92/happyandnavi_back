package com.happyandnavi.mapper;

import com.happyandnavi.domain.Promise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * =========================================
 * PromiseMapper 인터페이스
 * =========================================
 * 
 * 약속일기/일정(Promise) 테이블에 대한 데이터 액세스를 담당하는 MyBatis Mapper입니다.
 * 
 * XML 매퍼 파일 위치: resources/mapper/PromiseMapper.xml
 */
@Mapper
public interface PromiseMapper {
    
    // ========================================
    // CREATE (등록)
    // ========================================
    
    /**
     * 새로운 일정을 등록합니다.
     * 
     * @param promise 등록할 일정 정보
     * @return 영향받은 행 수
     */
    int insert(Promise promise);
    
    // ========================================
    // READ (조회)
    // ========================================
    
    /**
     * 일정 ID로 조회합니다.
     * 
     * @param promiseId 일정 고유 번호
     * @return 일정 정보
     */
    Optional<Promise> findById(@Param("promiseId") Long promiseId);
    
    /**
     * 특정 사용자의 모든 일정을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 일정 목록
     */
    List<Promise> findAllByUserId(@Param("userId") Long userId);
    
    /**
     * 특정 사용자의 특정 연월 일정을 조회합니다. (캘린더용)
     * 
     * @param userId 사용자 ID
     * @param year 연도
     * @param month 월 (1~12)
     * @return 일정 목록
     */
    List<Promise> findByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );
    
    /**
     * 특정 기간의 일정을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 일정 목록
     */
    List<Promise> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * 오늘의 일정을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 오늘 일정 목록
     */
    List<Promise> findTodayByUserId(@Param("userId") Long userId);
    
    /**
     * 다가오는 일정을 조회합니다. (오늘 이후)
     * 
     * @param userId 사용자 ID
     * @param limit 조회할 개수
     * @return 다가오는 일정 목록
     */
    List<Promise> findUpcomingByUserId(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );
    
    /**
     * 알림이 필요한 일정을 조회합니다.
     * 현재 시간 기준으로 알림 시간이 도래한 일정을 조회합니다.
     * 
     * @param currentTime 현재 시간
     * @return 알림 대상 일정 목록
     */
    List<Promise> findForReminder(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 특정 사용자의 일정 총 개수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 일정 개수
     */
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 제목으로 일정을 검색합니다. (캘린더 검색)
     * 
     * @param userId 사용자 ID
     * @param keyword 검색 키워드
     * @return 일정 목록
     */
    List<Promise> searchByTitle(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );
    
    // ========================================
    // UPDATE (수정)
    // ========================================
    
    /**
     * 일정을 수정합니다.
     * 
     * @param promise 수정할 일정 정보
     * @return 영향받은 행 수
     */
    int update(Promise promise);
    
    // ========================================
    // DELETE (삭제)
    // ========================================
    
    /**
     * 일정을 논리적으로 삭제합니다.
     * 
     * @param promiseId 일정 ID
     * @return 영향받은 행 수
     */
    int deleteById(@Param("promiseId") Long promiseId);
    
    /**
     * 특정 사용자의 모든 일정을 논리적으로 삭제합니다.
     * 회원 탈퇴 시 사용됩니다.
     * 
     * @param userId 사용자 ID
     * @return 영향받은 행 수
     */
    int deleteAllByUserId(@Param("userId") Long userId);
}
