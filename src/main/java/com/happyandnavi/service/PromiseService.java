package com.happyandnavi.service;

import com.happyandnavi.dto.promise.PromiseDto;

import java.util.List;

/**
 * =========================================
 * PromiseService 인터페이스
 * =========================================
 * 
 * 일정(약속일기) 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 */
public interface PromiseService {
    
    /**
     * 일정 등록
     */
    PromiseDto.Response createPromise(Long userId, PromiseDto.CreateRequest request);
    
    /**
     * 일정 상세 조회
     */
    PromiseDto.Response getPromise(Long promiseId, Long userId);
    
    /**
     * 일정 전체 조회
     */
    List<PromiseDto.Response> getAllPromises(Long userId);
    
    /**
     * 월별 캘린더 일정 조회
     */
    List<PromiseDto.CalendarItem> getCalendarData(Long userId, int year, int month);
    
    /**
     * 오늘의 일정 조회
     */
    List<PromiseDto.Response> getTodayPromises(Long userId);
    
    /**
     * 다가오는 일정 조회
     */
    List<PromiseDto.Response> getUpcomingPromises(Long userId, int limit);
    
    /**
     * 일정 검색
     */
    List<PromiseDto.Response> searchPromises(Long userId, String keyword);
    
    /**
     * 일정 수정
     */
    PromiseDto.Response updatePromise(Long promiseId, Long userId, PromiseDto.UpdateRequest request);
    
    /**
     * 일정 삭제
     */
    void deletePromise(Long promiseId, Long userId);
}
