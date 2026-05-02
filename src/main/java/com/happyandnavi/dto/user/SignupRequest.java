package com.happyandnavi.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * =========================================
 * 회원가입 요청 DTO
 * =========================================
 * 
 * 클라이언트에서 회원가입 시 전달하는 데이터를 담는 DTO입니다.
 * Jakarta Validation을 사용하여 입력값 유효성을 검증합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    
    /**
     * 이메일 주소 (로그인 ID로 사용)
     * 
     * @NotBlank: null, 빈 문자열, 공백만 있는 문자열 불가
     * @Email: 이메일 형식 검증
     * @Size: 최소 5자, 최대 40자
     */
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(min = 5, max = 40, message = "이메일은 5~40자 사이여야 합니다.")
    private String email;
    
    /**
     * 비밀번호
     * 
     * @NotBlank: 필수 입력
     * @Size: 최소 8자, 최대 20자
     * @Pattern: 영문, 숫자, 특수문자 포함 필수
     */
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
        message = "비밀번호는 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
    )
    private String password;
    
    /**
     * 비밀번호 확인
     * 프론트엔드에서 password와 일치 여부를 1차 검증하고,
     * 백엔드에서도 추가 검증합니다.
     */
    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    private String passwordConfirm;
    
    /**
     * 연락처 (휴대폰 번호)
     * 
     * @Pattern: 010-1234-5678 또는 01012345678 형식
     */
    @NotBlank(message = "연락처는 필수 입력 항목입니다.")
    @Pattern(
        regexp = "^01[0-9]-?\\d{3,4}-?\\d{4}$",
        message = "올바른 휴대폰 번호 형식이 아닙니다."
    )
    private String phone;
    
    /**
     * 반려동물 이름 (프로필명)
     * 
     * @Size: 최소 1자, 최대 30자
     */
    @NotBlank(message = "반려동물 이름은 필수 입력 항목입니다.")
    @Size(min = 1, max = 30, message = "반려동물 이름은 1~30자 사이여야 합니다.")
    private String petName;
}
