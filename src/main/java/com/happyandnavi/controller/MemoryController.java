package com.happyandnavi.controller;

import com.happyandnavi.dto.common.ApiResponse;
import com.happyandnavi.dto.common.PageResponse;
import com.happyandnavi.dto.memory.MemoryDto;
import com.happyandnavi.service.MemoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * =========================================
 * MemoryController 클래스
 * =========================================
 * 
 * 추억일기 관련 API 엔드포인트를 제공하는 컨트롤러입니다.
 * 
 * 제공하는 API:
 * - POST /api/memories : 추억일기 등록
 * - GET /api/memories : 추억일기 전체 조회 (페이징)
 * - GET /api/memories/{memoryId} : 추억일기 상세 조회
 * - GET /api/memories/calendar : 월별 캘린더 데이터 조회
 * - GET /api/memories/year/{year} : 연도별 추억일기 조회
 * - GET /api/memories/search : 추억일기 검색
 * - PUT /api/memories/{memoryId} : 추억일기 수정
 * - DELETE /api/memories/{memoryId} : 추억일기 삭제
 */
@Slf4j
@RestController
@RequestMapping("/api/memories")
@RequiredArgsConstructor
public class MemoryController {
    
    private final MemoryService memoryService;
    
    /**
     * 추억일기 등록 API
     * 
     * multipart/form-data 형식으로 이미지와 함께 데이터를 전송합니다.
     * 
     * @param userId 인증된 사용자 ID
     * @param request 등록 요청 데이터
     * @param image 업로드할 이미지 파일
     * @return 생성된 추억일기 정보
     * 
     * consumes: 요청 Content-Type 지정 (파일 업로드용)
     * @RequestPart: multipart 요청에서 특정 파트 추출
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MemoryDto.Response>> createMemory(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestPart("data") MemoryDto.CreateRequest request,
            @RequestPart("image") MultipartFile image) {
        log.info("추억일기 등록: userId={}, date={}", userId, request.getMemoryDate());
        
        MemoryDto.Response response = memoryService.createMemory(userId, request, image);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "추억이 등록되었습니다."));
    }
    
    /**
     * 추억일기 전체 조회 API (페이징)
     * 
     * @param userId 인증된 사용자 ID
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 페이징된 추억일기 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MemoryDto.Response>>> getMemories(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("추억일기 전체 조회: userId={}, page={}, size={}", userId, page, size);
        
        PageResponse<MemoryDto.Response> response = memoryService.getMemories(userId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 추억일기 상세 조회 API
     * 
     * @param userId 인증된 사용자 ID
     * @param memoryId 추억일기 ID
     * @return 추억일기 상세 정보
     * 
     * @PathVariable: URL 경로 변수 추출
     */
    @GetMapping("/{memoryId}")
    public ResponseEntity<ApiResponse<MemoryDto.Response>> getMemory(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long memoryId) {
        log.info("추억일기 상세 조회: userId={}, memoryId={}", userId, memoryId);
        
        MemoryDto.Response response = memoryService.getMemory(memoryId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 월별 캘린더 데이터 조회 API
     * 
     * 캘린더 UI에 표시할 썸네일 데이터를 반환합니다.
     * 
     * @param userId 인증된 사용자 ID
     * @param year 연도
     * @param month 월 (1~12)
     * @return 캘린더 아이템 목록
     */
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<MemoryDto.CalendarItem>>> getCalendarData(
            @AuthenticationPrincipal Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        log.info("캘린더 데이터 조회: userId={}, year={}, month={}", userId, year, month);
        
        List<MemoryDto.CalendarItem> response = memoryService.getCalendarData(userId, year, month);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 연도별 추억일기 조회 API
     * 
     * @param userId 인증된 사용자 ID
     * @param year 연도
     * @return 해당 연도의 추억일기 목록
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<MemoryDto.Response>>> getMemoriesByYear(
            @AuthenticationPrincipal Long userId,
            @PathVariable int year) {
        log.info("연도별 추억일기 조회: userId={}, year={}", userId, year);
        
        List<MemoryDto.Response> response = memoryService.getMemoriesByYear(userId, year);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 추억일기 검색 API
     * 
     * 코멘트 내용으로 검색합니다.
     * 
     * @param userId 인증된 사용자 ID
     * @param keyword 검색 키워드
     * @return 검색 결과 목록
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemoryDto.Response>>> searchMemories(
            @AuthenticationPrincipal Long userId,
            @RequestParam String keyword) {
        log.info("추억일기 검색: userId={}, keyword={}", userId, keyword);
        
        List<MemoryDto.Response> response = memoryService.searchMemories(userId, keyword);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 추억일기 수정 API
     * 
     * @param userId 인증된 사용자 ID
     * @param memoryId 추억일기 ID
     * @param request 수정 요청 데이터
     * @param image 새 이미지 파일 (선택)
     * @return 수정된 추억일기 정보
     */
    @PutMapping(value = "/{memoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MemoryDto.Response>> updateMemory(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long memoryId,
            @Valid @RequestPart("data") MemoryDto.UpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info("추억일기 수정: userId={}, memoryId={}", userId, memoryId);
        
        MemoryDto.Response response = memoryService.updateMemory(memoryId, userId, request, image);
        
        return ResponseEntity.ok(ApiResponse.success(response, "추억이 수정되었습니다."));
    }
    
    /**
     * 추억일기 삭제 API
     * 
     * @param userId 인증된 사용자 ID
     * @param memoryId 추억일기 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{memoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteMemory(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long memoryId) {
        log.info("추억일기 삭제: userId={}, memoryId={}", userId, memoryId);
        
        memoryService.deleteMemory(memoryId, userId);
        
        return ResponseEntity.ok(ApiResponse.success("추억이 삭제되었습니다."));
    }
}
