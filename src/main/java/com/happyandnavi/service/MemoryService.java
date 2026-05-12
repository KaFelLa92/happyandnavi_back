package com.happyandnavi.service;

import com.happyandnavi.dto.common.PageResponse;
import com.happyandnavi.dto.memory.MemoryDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * =========================================
 * MemoryService 인터페이스
 * =========================================
 * 
 * 추억일기 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 */
public interface MemoryService {
    
    /**
     * 추억일기 등록
     * 
     * @param userId 사용자 ID
     * @param request 등록 요청 데이터
     * @param image 업로드할 이미지 파일
     * @return 생성된 추억일기 정보
     */
    MemoryDto.Response createMemory(Long userId, MemoryDto.CreateRequest request, MultipartFile image);
    
    /**
     * 추억일기 상세 조회
     * 
     * @param memoryId 추억일기 ID
     * @param userId 요청 사용자 ID (권한 확인용)
     * @return 추억일기 상세 정보
     */
    MemoryDto.Response getMemory(Long memoryId, Long userId);
    
    /**
     * 추억일기 전체 조회 (페이징)
     * 
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 추억일기 목록
     */
    PageResponse<MemoryDto.Response> getMemories(Long userId, int page, int size);
    
    /**
     * 월별 캘린더 데이터 조회
     * 
     * @param userId 사용자 ID
     * @param year 연도
     * @param month 월 (1~12)
     * @return 캘린더 아이템 목록
     */
    List<MemoryDto.CalendarItem> getCalendarData(Long userId, int year, int month);
    
    /**
     * 연도별 추억일기 조회
     * 
     * @param userId 사용자 ID
     * @param year 연도
     * @return 해당 연도의 추억일기 목록
     */
    List<MemoryDto.Response> getMemoriesByYear(Long userId, int year);
    
    /**
     * 추억일기 검색
     * 
     * @param userId 사용자 ID
     * @param keyword 검색 키워드
     * @return 검색 결과 목록
     */
    List<MemoryDto.Response> searchMemories(Long userId, String keyword);
    
    /**
     * 추억일기 수정
     * 
     * @param memoryId 추억일기 ID
     * @param userId 요청 사용자 ID
     * @param request 수정 요청 데이터
     * @return 수정된 추억일기 정보
     */
    MemoryDto.Response updateMemory(Long memoryId, Long userId, MemoryDto.UpdateRequest request);
    
    /**
     * 추억일기 삭제
     * 
     * @param memoryId 추억일기 ID
     * @param userId 요청 사용자 ID
     */
    void deleteMemory(Long memoryId, Long userId);
}
