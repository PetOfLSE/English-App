package com.example.english.auth.service;

import com.example.english.auth.controller.request.LoginRequest;
import com.example.english.auth.controller.request.RefreshRequest;
import com.example.english.auth.controller.request.RegisterRequest;
import com.example.english.common.enums.Role;
import com.example.english.common.jwt.JwtDto;
import com.example.english.common.jwt.JwtProvider;
import com.example.english.domain.db.entity.User;
import com.example.english.domain.db.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    public User register(@Valid RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("이미 등록된 사용자 이메일입니다.");
        }
        User user = toUser(registerRequest);
        return userRepository.save(user);
    }

    public User toUser(RegisterRequest registerRequest) {
        return User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .name(registerRequest.getName())
                .role(Role.ROLE_USER)
                .build();
    }

    public JwtDto login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        String name = authenticationToken.getName();
        log.info("name: {}", name);

        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return jwtProvider.generateToken(authenticate);
    }

    public JwtDto refresh(RefreshRequest refreshRequest) {
        String token = refreshRequest.getRefreshToken();
        boolean expired = jwtProvider.isExpired(token);

        if (!expired) {
            try{
                Authentication authentication = jwtProvider.getAuthentication(token);
                log.info("authentication: {}", authentication.getName());
                return jwtProvider.generateRefresh(authentication);
            }catch (Exception e) {
                log.info("error: {}", e.getMessage());
            }
        }
        else{
            throw new RuntimeException("refresh token이 만료되었습니다.");
        }

        return null;
    }
}
