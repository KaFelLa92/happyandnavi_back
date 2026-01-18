package com.happyandnavi.controller;

import com.happyandnavi.dto.common.ApiResponse;
import com.happyandnavi.dto.promise.PromiseDto;
import com.happyandnavi.service.PromiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =========================================
 * PromiseController 클래스
 * =========================================
 * 
 * 일정(약속일기) 관련 API 엔드포인트를 제공하는 컨트롤러입니다.
 * 
 * 제공하는 API:
 * - POST /api/promises : 일정 등록
 * - GET /api/promises : 일정 전체 조회
 * - GET /api/promises/{promiseId} : 일정 상세 조회
 * - GET /api/promises/calendar : 월별 캘린더 일정 조회
 * - GET /api/promises/today : 오늘의 일정 조회
 * - GET /api/promises/upcoming : 다가오는 일정 조회
 * - GET /api/promises/search : 일정 검색
 * - PUT /api/promises/{promiseId} : 일정 수정
 * - DELETE /api/promises/{promiseId} : 일정 삭제
 */
@Slf4j
@RestController
@RequestMapping("/api/promises")
@RequiredArgsConstructor
public class PromiseController {
    
    private final PromiseService promiseService;
    
    /**
     * 일정 등록 API
     * 
     * @param userId 인증된 사용자 ID
     * @param request 등록 요청 데이터
     * @return 생성된 일정 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PromiseDto.Response>> createPromise(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PromiseDto.CreateRequest request) {
        log.info("일정 등록: userId={}, title={}", userId, request.getPromiseTitle());
        
        PromiseDto.Response response = promiseService.createPromise(userId, request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "일정이 등록되었습니다."));
    }
    
    /**
     * 일정 전체 조회 API
     * 
     * @param userId 인증된 사용자 ID
     * @return 모든 일정 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PromiseDto.Response>>> getAllPromises(
            @AuthenticationPrincipal Long userId) {
        log.info("일정 전체 조회: userId={}", userId);
        
        List<PromiseDto.Response> response = promiseService.getAllPromises(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 일정 상세 조회 API
     * 
     * @param userId 인증된 사용자 ID
     * @param promiseId 일정 ID
     * @return 일정 상세 정보
     */
    @GetMapping("/{promiseId}")
    public ResponseEntity<ApiResponse<PromiseDto.Response>> getPromise(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long promiseId) {
        log.info("일정 상세 조회: userId={}, promiseId={}", userId, promiseId);
        
        PromiseDto.Response response = promiseService.getPromise(promiseId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 월별 캘린더 일정 조회 API
     * 
     * @param userId 인증된 사용자 ID
     * @param year 연도
     * @param month 월 (1~12)
     * @return 캘린더 일정 목록
     */
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<PromiseDto.CalendarItem>>> getCalendarData(
            @AuthenticationPrincipal Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        log.info("일정 캘린더 조회: userId={}, year={}, month={}", userId, year, month);
        
        List<PromiseDto.CalendarItem> response = promiseService.getCalendarData(userId, year, month);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 오늘의 일정 조회 API
     * 
     * @param userId 인증된 사용자 ID
     * @return 오늘 일정 목록
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<PromiseDto.Response>>> getTodayPromises(
            @AuthenticationPrincipal Long userId) {
        log.info("오늘 일정 조회: userId={}", userId);
        
        List<PromiseDto.Response> response = promiseService.getTodayPromises(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 다가오는 일정 조회 API
     * 
     * @param userId 인증된 사용자 ID
     * @param limit 조회할 개수 (기본값: 5)
     * @return 다가오는 일정 목록
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<PromiseDto.Response>>> getUpcomingPromises(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        log.info("다가오는 일정 조회: userId={}, limit={}", userId, limit);
        
        List<PromiseDto.Response> response = promiseService.getUpcomingPromises(userId, limit);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 일정 검색 API
     * 
     * @param userId 인증된 사용자 ID
     * @param keyword 검색 키워드
     * @return 검색 결과 목록
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PromiseDto.Response>>> searchPromises(
            @AuthenticationPrincipal Long userId,
            @RequestParam String keyword) {
        log.info("일정 검색: userId={}, keyword={}", userId, keyword);
        
        List<PromiseDto.Response> response = promiseService.searchPromises(userId, keyword);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * 일정 수정 API
     * 
     * @param userId 인증된 사용자 ID
     * @param promiseId 일정 ID
     * @param request 수정 요청 데이터
     * @return 수정된 일정 정보
     */
    @PutMapping("/{promiseId}")
    public ResponseEntity<ApiResponse<PromiseDto.Response>> updatePromise(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long promiseId,
            @Valid @RequestBody PromiseDto.UpdateRequest request) {
        log.info("일정 수정: userId={}, promiseId={}", userId, promiseId);
        
        PromiseDto.Response response = promiseService.updatePromise(promiseId, userId, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "일정이 수정되었습니다."));
    }
    
    /**
     * 일정 삭제 API
     * 
     * @param userId 인증된 사용자 ID
     * @param promiseId 일정 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/{promiseId}")
    public ResponseEntity<ApiResponse<Void>> deletePromise(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long promiseId) {
        log.info("일정 삭제: userId={}, promiseId={}", userId, promiseId);
        
        promiseService.deletePromise(promiseId, userId);
        
        return ResponseEntity.ok(ApiResponse.success("일정이 삭제되었습니다."));
    }
}
