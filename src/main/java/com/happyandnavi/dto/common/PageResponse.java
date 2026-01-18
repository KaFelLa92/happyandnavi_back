package com.happyandnavi.dto.common;

import lombok.*;
import java.util.List;

/**
 * =========================================
 * 페이지네이션 응답 DTO
 * =========================================
 * 
 * 페이징 처리된 목록 조회 결과를 담는 DTO입니다.
 * 캘린더의 연/월 단위 페이징에 사용됩니다.
 * 
 * @param <T> 목록 항목의 타입
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    
    /**
     * 현재 페이지의 데이터 목록
     */
    private List<T> content;
    
    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    private int page;
    
    /**
     * 페이지당 항목 수
     */
    private int size;
    
    /**
     * 전체 항목 수
     */
    private long totalElements;
    
    /**
     * 전체 페이지 수
     */
    private int totalPages;
    
    /**
     * 첫 페이지 여부
     */
    private boolean first;
    
    /**
     * 마지막 페이지 여부
     */
    private boolean last;
    
    /**
     * 현재 페이지에 데이터가 있는지 여부
     */
    private boolean empty;
    
    // ========================================
    // 정적 팩토리 메서드
    // ========================================
    
    /**
     * 페이지 응답 객체 생성
     * 
     * @param content 데이터 목록
     * @param page 현재 페이지 (0부터 시작)
     * @param size 페이지 크기
     * @param totalElements 전체 항목 수
     * @param <T> 데이터 타입
     * @return 페이지 응답 객체
     */
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return PageResponse.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .empty(content == null || content.isEmpty())
                .build();
    }
}
