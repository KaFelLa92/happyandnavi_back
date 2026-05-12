package com.happyandnavi.service.impl;

import com.happyandnavi.domain.Memory;
import com.happyandnavi.dto.common.PageResponse;
import com.happyandnavi.dto.memory.MemoryDto;
import com.happyandnavi.exception.BusinessException;
import com.happyandnavi.mapper.MemoryMapper;
import com.happyandnavi.service.MemoryService;
import com.happyandnavi.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * =========================================
 * MemoryServiceImpl 구현체
 * =========================================
 * 
 * 추억일기 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoryServiceImpl implements MemoryService {
    
    // ========================================
    // 의존성 주입
    // ========================================
    
    private final MemoryMapper memoryMapper;
    private final FileUploadUtil fileUploadUtil;
    
    /**
     * 이미지 기본 URL (application.properties에서 설정)
     */
    @Value("${file.upload.base-url:http://localhost:8080/uploads}")
    private String baseUrl;
    
    // ========================================
    // CREATE (등록)
    // ========================================
    
    /**
     * 추억일기 등록
     * 
     * 1. 같은 날짜에 이미 추억이 있는지 확인
     * 2. 이미지 파일 업로드
     * 3. 추억일기 데이터 저장
     */
    @Override
    @Transactional
    public MemoryDto.Response createMemory(Long userId, MemoryDto.CreateRequest request, MultipartFile image) {
        log.info("추억일기 등록: userId={}, date={}", userId, request.getMemoryDate());
        
        // 1. 같은 날짜 중복 확인 (하루에 하나의 추억만 허용)
        if (memoryMapper.existsByUserIdAndDate(userId, request.getMemoryDate()) > 0) {
            throw new BusinessException("해당 날짜에 이미 추억이 등록되어 있습니다.", 400);
        }
        
        // 2. 이미지 업로드 (필수)
        if (image == null || image.isEmpty()) {
            throw new BusinessException("추억 사진은 필수입니다.", 400);
        }
        
        // 파일 업로드 및 경로 반환
        String imagePath = fileUploadUtil.uploadMemoryMedia(userId, image);
        
        // 3. 엔티티 생성 및 저장
        Memory memory = request.toEntity(userId, imagePath);
        memoryMapper.insert(memory);
        
        log.info("추억일기 등록 완료: memoryId={}", memory.getMemoryId());
        
        // 4. 저장된 데이터 다시 조회하여 응답 반환
        return memoryMapper.findById(memory.getMemoryId())
                .map(m -> MemoryDto.Response.fromEntity(m, baseUrl))
                .orElseThrow(() -> new BusinessException("추억일기 등록에 실패했습니다.", 500));
    }
    
    // ========================================
    // READ (조회)
    // ========================================
    
    /**
     * 추억일기 상세 조회
     */
    @Override
    public MemoryDto.Response getMemory(Long memoryId, Long userId) {
        Memory memory = memoryMapper.findById(memoryId)
                .orElseThrow(() -> new BusinessException("추억일기를 찾을 수 없습니다.", 404));
        
        // 본인의 추억만 조회 가능
        if (!memory.getUserId().equals(userId)) {
            throw new BusinessException("접근 권한이 없습니다.", 403);
        }
        
        return MemoryDto.Response.fromEntity(memory, baseUrl);
    }
    
    /**
     * 추억일기 전체 조회 (페이징)
     */
    @Override
    public PageResponse<MemoryDto.Response> getMemories(Long userId, int page, int size) {
        // offset 계산
        int offset = page * size;
        
        // 데이터 조회
        List<Memory> memories = memoryMapper.findByUserIdWithPaging(userId, offset, size);
        long total = memoryMapper.countByUserId(userId);
        
        // DTO 변환
        List<MemoryDto.Response> content = memories.stream()
                .map(m -> MemoryDto.Response.fromEntity(m, baseUrl))
                .collect(Collectors.toList());
        
        return PageResponse.of(content, page, size, total);
    }
    
    /**
     * 월별 캘린더 데이터 조회
     * 캘린더 UI에 표시할 썸네일 데이터를 반환합니다.
     */
    @Override
    public List<MemoryDto.CalendarItem> getCalendarData(Long userId, int year, int month) {
        log.debug("캘린더 데이터 조회: userId={}, year={}, month={}", userId, year, month);
        
        List<Memory> memories = memoryMapper.findByUserIdAndYearMonth(userId, year, month);
        
        return memories.stream()
                .map(m -> MemoryDto.CalendarItem.fromEntity(m, baseUrl))
                .collect(Collectors.toList());
    }
    
    /**
     * 연도별 추억일기 조회
     */
    @Override
    public List<MemoryDto.Response> getMemoriesByYear(Long userId, int year) {
        List<Memory> memories = memoryMapper.findByUserIdAndYear(userId, year);
        
        return memories.stream()
                .map(m -> MemoryDto.Response.fromEntity(m, baseUrl))
                .collect(Collectors.toList());
    }
    
    /**
     * 추억일기 검색 (코멘트 기준)
     */
    @Override
    public List<MemoryDto.Response> searchMemories(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException("검색어를 입력해주세요.", 400);
        }
        
        List<Memory> memories = memoryMapper.searchByComment(userId, keyword.trim());
        
        return memories.stream()
                .map(m -> MemoryDto.Response.fromEntity(m, baseUrl))
                .collect(Collectors.toList());
    }
    
    // ========================================
    // UPDATE (수정)
    // ========================================
    
    /**
     * 추억일기 수정
     */
    @Override
    @Transactional
    public MemoryDto.Response updateMemory(Long memoryId, Long userId, MemoryDto.UpdateRequest request) {
        log.info("추억일기 수정: memoryId={}, userId={}", memoryId, userId);
        
        // 기존 추억 조회
        Memory memory = memoryMapper.findById(memoryId)
                .orElseThrow(() -> new BusinessException("추억일기를 찾을 수 없습니다.", 404));
        
        // 본인의 추억만 수정 가능
        if (!memory.getUserId().equals(userId)) {
            throw new BusinessException("수정 권한이 없습니다.", 403);
        }
        
        // 수정할 필드 업데이트
        if (request.getMemoryDate() != null) {
            memory.setMemoryDate(request.getMemoryDate());
        }
        if (request.getMemoryComment() != null) {
            memory.setMemoryComment(request.getMemoryComment());
        }
        if (request.getMemoryWeather() != null) {
            memory.setMemoryWeather(request.getMemoryWeather());
        }
        if (request.getUserMood() != null) {
            memory.setUserMood(request.getUserMood());
        }
        if (request.getPetMood() != null) {
            memory.setPetMood(request.getPetMood());
        }
        
        // 데이터베이스 업데이트
        memoryMapper.update(memory);
        log.info("추억일기 수정 완료: memoryId={}", memoryId);
        
        return MemoryDto.Response.fromEntity(memory, baseUrl);
    }
    
    // ========================================
    // DELETE (삭제)
    // ========================================
    
    /**
     * 추억일기 삭제 (논리적 삭제)
     */
    @Override
    @Transactional
    public void deleteMemory(Long memoryId, Long userId) {
        log.info("추억일기 삭제: memoryId={}, userId={}", memoryId, userId);
        
        // 기존 추억 조회
        Memory memory = memoryMapper.findById(memoryId)
                .orElseThrow(() -> new BusinessException("추억일기를 찾을 수 없습니다.", 404));
        
        // 본인의 추억만 삭제 가능
        if (!memory.getUserId().equals(userId)) {
            throw new BusinessException("삭제 권한이 없습니다.", 403);
        }
        
        // 논리적 삭제
        memoryMapper.deleteById(memoryId);
        log.info("추억일기 삭제 완료: memoryId={}", memoryId);
    }
}
