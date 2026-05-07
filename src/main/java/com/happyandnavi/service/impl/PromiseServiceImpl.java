package com.happyandnavi.service.impl;

import com.happyandnavi.domain.Promise;
import com.happyandnavi.dto.promise.PromiseDto;
import com.happyandnavi.exception.BusinessException;
import com.happyandnavi.mapper.PromiseMapper;
import com.happyandnavi.service.PromiseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * =========================================
 * PromiseServiceImpl 구현체
 * =========================================
 * 
 * 일정(약속일기) 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromiseServiceImpl implements PromiseService {
    
    private final PromiseMapper promiseMapper;
    
    @Override
    @Transactional
    public PromiseDto.Response createPromise(Long userId, PromiseDto.CreateRequest request) {
        log.info("일정 등록: userId={}, title={}", userId, request.getPromiseTitle());
        
        // 엔티티 생성 및 저장
        Promise promise = request.toEntity(userId);
        promiseMapper.insert(promise);
        
        log.info("일정 등록 완료: promiseId={}", promise.getPromiseId());
        
        return promiseMapper.findById(promise.getPromiseId())
                .map(PromiseDto.Response::fromEntity)
                .orElseThrow(() -> new BusinessException("일정 등록에 실패했습니다.", 500));
    }
    
    @Override
    public PromiseDto.Response getPromise(Long promiseId, Long userId) {
        Promise promise = promiseMapper.findById(promiseId)
                .orElseThrow(() -> new BusinessException("일정을 찾을 수 없습니다.", 404));
        
        // 본인의 일정만 조회 가능
        if (!promise.getUserId().equals(userId)) {
            throw new BusinessException("접근 권한이 없습니다.", 403);
        }
        
        return PromiseDto.Response.fromEntity(promise);
    }
    
    @Override
    public List<PromiseDto.Response> getAllPromises(Long userId) {
        return promiseMapper.findAllByUserId(userId).stream()
                .map(PromiseDto.Response::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PromiseDto.CalendarItem> getCalendarData(Long userId, int year, int month) {
        log.debug("일정 캘린더 데이터 조회: userId={}, year={}, month={}", userId, year, month);
        
        return promiseMapper.findByUserIdAndYearMonth(userId, year, month).stream()
                .map(PromiseDto.CalendarItem::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PromiseDto.Response> getTodayPromises(Long userId) {
        return promiseMapper.findTodayByUserId(userId).stream()
                .map(PromiseDto.Response::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PromiseDto.Response> getUpcomingPromises(Long userId, int limit) {
        return promiseMapper.findUpcomingByUserId(userId, limit).stream()
                .map(PromiseDto.Response::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PromiseDto.Response> searchPromises(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException("검색어를 입력해주세요.", 400);
        }
        
        return promiseMapper.searchByTitle(userId, keyword.trim()).stream()
                .map(PromiseDto.Response::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PromiseDto.Response updatePromise(Long promiseId, Long userId, PromiseDto.UpdateRequest request) {
        log.info("일정 수정: promiseId={}, userId={}", promiseId, userId);
        
        Promise promise = promiseMapper.findById(promiseId)
                .orElseThrow(() -> new BusinessException("일정을 찾을 수 없습니다.", 404));
        
        if (!promise.getUserId().equals(userId)) {
            throw new BusinessException("수정 권한이 없습니다.", 403);
        }
        
        // 필드 업데이트
        if (request.getPromiseTitle() != null) promise.setPromiseTitle(request.getPromiseTitle());
        if (request.getPromiseIconPath() != null) promise.setPromiseIconPath(request.getPromiseIconPath());
        if (request.getPromiseColor() != null) promise.setPromiseColor(request.getPromiseColor());
        if (request.getPromiseCategory() != null) promise.setPromiseCategory(request.getPromiseCategory());
        if (request.getPromiseComment() != null) promise.setPromiseComment(request.getPromiseComment());
        if (request.getPromiseStart() != null) promise.setPromiseStart(request.getPromiseStart());
        if (request.getPromiseEnd() != null) promise.setPromiseEnd(request.getPromiseEnd());
        if (request.getReminderMinutes() != null) promise.setReminderMinutes(request.getReminderMinutes());
        if (request.getRepeatType() != null) promise.setRepeatType(request.getRepeatType());
        if (request.getAllDay() != null) promise.setAllDay(request.getAllDay());
        
        promiseMapper.update(promise);
        log.info("일정 수정 완료: promiseId={}", promiseId);
        
        return PromiseDto.Response.fromEntity(promise);
    }
    
    @Override
    @Transactional
    public void deletePromise(Long promiseId, Long userId) {
        log.info("일정 삭제: promiseId={}, userId={}", promiseId, userId);
        
        Promise promise = promiseMapper.findById(promiseId)
                .orElseThrow(() -> new BusinessException("일정을 찾을 수 없습니다.", 404));
        
        if (!promise.getUserId().equals(userId)) {
            throw new BusinessException("삭제 권한이 없습니다.", 403);
        }
        
        promiseMapper.deleteById(promiseId);
        log.info("일정 삭제 완료: promiseId={}", promiseId);
    }
}
