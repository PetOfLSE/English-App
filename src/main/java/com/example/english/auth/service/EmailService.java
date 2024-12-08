package com.example.english.auth.service;

import com.example.english.auth.controller.request.EmailCheckRequest;
import com.example.english.auth.controller.request.EmailSendRequest;
import com.example.english.auth.controller.response.EmailCheckResponse;
import com.example.english.auth.controller.response.EmailSendResponse;
import com.example.english.domain.db.entity.EmailInfo;
import com.example.english.domain.db.repository.EmailInfoRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailInfoRepository emailInfoRepository;

    private final int AUTH_CODE_LENGTH = 6;

    public String generateAuthCode(){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder result = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < AUTH_CODE_LENGTH; i++) {
            result.append(chars[secureRandom.nextInt(chars.length)]);
        }

        return result.toString();
    }

    public EmailSendResponse send(EmailSendRequest request) throws MessagingException {
        String from = "silwerhar123@gmail.com";
        String to = request.getEmail();
        String title = "가입 인증번호";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.addRecipients(MimeMessage.RecipientType.TO, to);
        mimeMessage.setSubject(title);

        String authCode = generateAuthCode();

        String content = "";
        content += "이메일 인증번호를 전달드립니다.\n";
        content += "인증 번호: " + authCode;

        mimeMessage.setFrom(from);
        mimeMessage.setText(content);

        mailSender.send(mimeMessage);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime expired = now.plusMinutes(3);

        EmailInfo info = EmailInfo.builder()
                .email(to)
                .authCode(authCode)
                .expiresAt(expired)
                .issuedAt(now)
                .build();

        emailInfoRepository.save(info);

        return EmailSendResponse.builder()
                .issuedAt(now)
                .expiresAt(expired)
                .authCode(authCode)
                .build();
    }

    public EmailCheckResponse verify(EmailCheckRequest request) {

        LocalDateTime now = LocalDateTime.now();
        EmailInfo emailInfo = emailInfoRepository.findByEmailOrderByIssuedAtDesc(request.getEmail());

        if(ChronoUnit.SECONDS.between(emailInfo.getIssuedAt(), now) > 180){
            throw new RuntimeException("인증번호 시간이 만료되었습니다,");
        }

        if(request.getAuthCode().equals(emailInfo.getAuthCode())){
            return EmailCheckResponse.builder()
                    .message("인증이 완료되었습니다.")
                    .build();
        }else{
            return EmailCheckResponse.builder()
                    .message("인증 번호가 틀렸습니다.")
                    .build();
        }
    }
}
