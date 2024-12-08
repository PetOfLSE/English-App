package com.example.english.domain.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "eml_auth")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    @Comment("이메일 기본값")
    private String email;

    @Column(name = "auth_code", nullable = false)
    @Comment("인증 코드")
    private String authCode;

    @Column(name = "issued_at", nullable = false)
    @Comment("요청 시간")
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    @Comment("만료 시간")
    private LocalDateTime expiresAt;
}
