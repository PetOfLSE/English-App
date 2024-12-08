package com.example.english.auth.controller.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginRequest {

    @NotBlank(message = "이메일은 필수 값 입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 값 입니다.")
    private String password;
}
