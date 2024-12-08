package com.example.english.auth.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterRequest {

    @NotBlank(message = "사용자 이름은 필수 값 입니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수 값 입니다.")
    private String password;

    @Email(message = "형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 값 입니다.")
    private String email;
}
