package com.happyandnavi.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * =========================================
 * 로그인 요청 DTO
 * =========================================
 * 
 * 클라이언트에서 로그인 시 전달하는 데이터를 담는 DTO입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    
    /**
     * 이메일 주소 (로그인 ID)
     */
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    /**
     * 비밀번호
     */
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}
