package com.happyandnavi.controller;

import com.happyandnavi.dto.common.ApiResponse;
import com.happyandnavi.dto.common.PageResponse;
import com.happyandnavi.dto.memory.MemoryDto;
import com.happyandnavi.service.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
     * 추억일기 등록 API (multipart/form-data, 평면 form 필드)
     *
     * 요청 form 필드:
     * - memoryDate    (필수)  yyyy-MM-dd
     * - memoryComment (선택)  최대 200자
     * - memoryWeather (선택)  1~5
     * - userMood      (선택)  1~5
     * - petMood       (선택)  1~5
     * - image         (필수)  이미지 파일
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MemoryDto.Response>> createMemory(
            @AuthenticationPrincipal Long userId,
            @RequestParam("memoryDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate memoryDate,
            @RequestParam(value = "memoryComment", required = false) String memoryComment,
            @RequestParam(value = "memoryWeather", required = false) Integer memoryWeather,
            @RequestParam(value = "userMood", required = false) Integer userMood,
            @RequestParam(value = "petMood", required = false) Integer petMood,
            @RequestPart("image") MultipartFile image) {
        log.info("추억일기 등록: userId={}, date={}", userId, memoryDate);

        // form 필드를 DTO로 조립 (검증은 DTO 필드 어노테이션이 아닌 서비스 단에서 수행 가능)
        MemoryDto.CreateRequest request = MemoryDto.CreateRequest.builder()
                .memoryDate(memoryDate)
                .memoryComment(memoryComment)
                .memoryWeather(memoryWeather)
                .userMood(userMood)
                .petMood(petMood)
                .build();

        MemoryDto.Response response = memoryService.createMemory(userId, request, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "추억이 등록되었습니다."));
    }

    /**
     * 추억일기 전체 조회 API (페이징)
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
     * 추억일기 수정 API (multipart/form-data, 평면 form 필드)
     *
     * 요청 form 필드 (모두 선택):
     * - memoryDate, memoryComment, memoryWeather, userMood, petMood, image
     */
    @PutMapping(value = "/{memoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MemoryDto.Response>> updateMemory(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long memoryId,
            @RequestParam(value = "memoryDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate memoryDate,
            @RequestParam(value = "memoryComment", required = false) String memoryComment,
            @RequestParam(value = "memoryWeather", required = false) Integer memoryWeather,
            @RequestParam(value = "userMood", required = false) Integer userMood,
            @RequestParam(value = "petMood", required = false) Integer petMood,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info("추억일기 수정: userId={}, memoryId={}", userId, memoryId);

        MemoryDto.UpdateRequest request = MemoryDto.UpdateRequest.builder()
                .memoryDate(memoryDate)
                .memoryComment(memoryComment)
                .memoryWeather(memoryWeather)
                .userMood(userMood)
                .petMood(petMood)
                .build();

        MemoryDto.Response response = memoryService.updateMemory(memoryId, userId, request, image);

        return ResponseEntity.ok(ApiResponse.success(response, "추억이 수정되었습니다."));
    }

    /**
     * 추억일기 삭제 API
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

