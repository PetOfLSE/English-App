package com.example.english.auth.controller;

import com.example.english.auth.controller.request.LoginRequest;
import com.example.english.auth.controller.request.RefreshRequest;
import com.example.english.auth.controller.request.RegisterRequest;
import com.example.english.auth.service.AuthService;
import com.example.english.common.jwt.JwtDto;
import com.example.english.domain.db.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "사용자 Controller")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "사용자 회원가입 API", description = """
                사용자 회원가입 API \n
                
                이메일 전송, 인증 API랑 섞어서 사용 가능 \n
                추후 변동사항 있을 수 있음 \n
                
                이미 가입된 이메일로는 가입이 불가능\n
                
                에러 핸들링 X \n
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "사용자 등록 요청 객체")
            @Valid @RequestBody RegisterRequest registerRequest
            ){

        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @Operation(summary = "사용자 로그인 API", description = "사용자 로그인 API 로그인 성공시 jwt 토큰 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JwtDto.class))
            })
    })
    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "사용자 로그인 요청 객체")
            @Valid @RequestBody LoginRequest loginRequest
    ){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "access 재발급 API", description = "refresh token을 넘기면 만료 기간 검증 후 access, refresh 둘 다 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JwtDto.class))
            })
    })
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestBody RefreshRequest refreshRequest){
        return ResponseEntity.ok(authService.refresh(refreshRequest));
    }
}
