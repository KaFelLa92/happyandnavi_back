package com.happyandnavi.mapper;

import com.happyandnavi.domain.Memory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * =========================================
 * MemoryMapper 인터페이스
 * =========================================
 * 
 * 추억일기(Memory) 테이블에 대한 데이터 액세스를 담당하는 MyBatis Mapper입니다.
 * 
 * XML 매퍼 파일 위치: resources/mapper/MemoryMapper.xml
 */
@Mapper
public interface MemoryMapper {
    
    // ========================================
    // CREATE (등록)
    // ========================================
    
    /**
     * 새로운 추억일기를 등록합니다.
     * 
     * @param memory 등록할 추억일기 정보
     * @return 영향받은 행 수
     */
    int insert(Memory memory);
    
    // ========================================
    // READ (조회)
    // ========================================
    
    /**
     * 추억일기 ID로 조회합니다.
     * 
     * @param memoryId 추억일기 고유 번호
     * @return 추억일기 정보
     */
    Optional<Memory> findById(@Param("memoryId") Long memoryId);
    
    /**
     * 특정 사용자의 특정 날짜 추억일기를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param memoryDate 추억 날짜
     * @return 추억일기 정보
     */
    Optional<Memory> findByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("memoryDate") LocalDate memoryDate
    );
    
    /**
     * 특정 사용자의 모든 추억일기를 조회합니다. (전체 조회)
     * 
     * @param userId 사용자 ID
     * @return 추억일기 목록
     */
    List<Memory> findAllByUserId(@Param("userId") Long userId);
    
    /**
     * 특정 사용자의 특정 연월 추억일기를 조회합니다. (캘린더용)
     * 
     * @param userId 사용자 ID
     * @param year 연도
     * @param month 월 (1~12)
     * @return 추억일기 목록
     */
    List<Memory> findByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );
    
    /**
     * 특정 사용자의 특정 연도 추억일기를 조회합니다. (연도별 조회)
     * 
     * @param userId 사용자 ID
     * @param year 연도
     * @return 추억일기 목록
     */
    List<Memory> findByUserIdAndYear(
            @Param("userId") Long userId,
            @Param("year") int year
    );
    
    /**
     * 특정 사용자의 추억일기를 페이징하여 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return 추억일기 목록
     */
    List<Memory> findByUserIdWithPaging(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );
    
    /**
     * 특정 사용자의 추억일기 총 개수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 추억일기 개수
     */
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 특정 사용자의 특정 연월 추억일기 개수를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param year 연도
     * @param month 월
     * @return 추억일기 개수
     */
    long countByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );
    
    /**
     * 특정 날짜에 이미 추억이 있는지 확인합니다.
     * 
     * @param userId 사용자 ID
     * @param memoryDate 확인할 날짜
     * @return 존재 여부 (1: 존재, 0: 미존재)
     */
    int existsByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("memoryDate") LocalDate memoryDate
    );
    
    /**
     * 코멘트로 추억일기를 검색합니다. (캘린더 검색)
     * 
     * @param userId 사용자 ID
     * @param keyword 검색 키워드
     * @return 추억일기 목록
     */
    List<Memory> searchByComment(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );
    
    // ========================================
    // UPDATE (수정)
    // ========================================
    
    /**
     * 추억일기를 수정합니다.
     * 
     * @param memory 수정할 추억일기 정보
     * @return 영향받은 행 수
     */
    int update(Memory memory);
    
    /**
     * 추억일기 이미지 경로를 수정합니다.
     * 
     * @param memoryId 추억일기 ID
     * @param memoryPath 새 이미지 경로
     * @return 영향받은 행 수
     */
    int updateMemoryPath(
            @Param("memoryId") Long memoryId,
            @Param("memoryPath") String memoryPath
    );
    
    // ========================================
    // DELETE (삭제)
    // ========================================
    
    /**
     * 추억일기를 논리적으로 삭제합니다.
     * 
     * @param memoryId 추억일기 ID
     * @return 영향받은 행 수
     */
    int deleteById(@Param("memoryId") Long memoryId);
    
    /**
     * 특정 사용자의 모든 추억일기를 논리적으로 삭제합니다.
     * 회원 탈퇴 시 사용됩니다.
     * 
     * @param userId 사용자 ID
     * @return 영향받은 행 수
     */
    int deleteAllByUserId(@Param("userId") Long userId);
}
