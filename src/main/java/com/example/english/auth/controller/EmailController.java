package com.example.english.auth.controller;

import com.example.english.auth.controller.request.EmailCheckRequest;
import com.example.english.auth.controller.request.EmailSendRequest;
import com.example.english.auth.controller.response.EmailCheckResponse;
import com.example.english.auth.controller.response.EmailSendResponse;
import com.example.english.auth.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
@Tag(name = "메일 Controller")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "메일 발송 API", description = "이메일 발송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EmailSendResponse.class))
            })
    })
    @PostMapping("/send")
    public ResponseEntity<EmailSendResponse> send(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "이메일 전송 요청 객체")
            @Valid @RequestBody EmailSendRequest request
    ) throws MessagingException
    {
        return ResponseEntity.ok(emailService.send(request));
    }

    @Operation(summary = "메일 인증 번호 검증 API", description = "이메일로 받은 인증 번호 검증 API 인증번호 제한시간 3분으로 설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EmailCheckResponse.class))
            })
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "인증 번호 검증 요청 객체")
            @Valid @RequestBody EmailCheckRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(emailService.verify(request));
    }

}
