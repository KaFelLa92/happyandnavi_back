package com.happyandnavi.dto.user;

import com.happyandnavi.domain.User;
import lombok.*;
import java.time.LocalDateTime;

/**
 * =========================================
 * 사용자 정보 응답 DTO
 * =========================================
 * 
 * 마이페이지 등에서 사용자 정보를 조회할 때 반환하는 DTO입니다.
 * 비밀번호 등 민감한 정보는 제외하고 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    
    /**
     * 사용자 고유 번호
     */
    private Long userId;
    
    /**
     * 이메일 주소
     */
    private String email;
    
    /**
     * 연락처
     */
    private String phone;
    
    /**
     * 반려동물 이름 (프로필명)
     */
    private String petName;

    /**
     * 반려동물 프로필 사진 (NULL 가능)
     */
    private String petPhotoUrl;
    
    /**
     * 일정 알림 설정
     * 1: 켜짐, 0: 꺼짐
     */
    private Integer scheduleSet;
    
    /**
     * 가입 방법
     * 1: 일반, 2: 카카오, 3: 구글
     */
    private Integer signupType;
    
    /**
     * 가입 방법 문자열 표현
     * "일반", "카카오", "구글"
     */
    private String signupTypeText;
    
    /**
     * 가입 일시
     */
    private LocalDateTime regDate;
    
    /**
     * User 엔티티를 UserResponse DTO로 변환하는 정적 메서드
     * 
     * @param user User 엔티티
     * @return UserResponse DTO
     */
    public static UserResponse fromEntity(User user, String baseUrl) {
        // 가입 방법을 문자열로 변환
        String signupTypeText = switch (user.getSignupType()) {
            case 2 -> "카카오";
            case 3 -> "구글";
            case 4 -> "네이버";
            case 5 -> "애플";
            default -> "일반";
        };

        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .petName(user.getPetName())
                .petPhotoUrl(user.getPetPhotoPath() != null
                        ? baseUrl + user.getPetPhotoPath() : null)
                .scheduleSet(user.getScheduleSet())
                .signupType(user.getSignupType())
                .signupTypeText(signupTypeText)
                .regDate(user.getRegDate())
                .build();
    }
}
