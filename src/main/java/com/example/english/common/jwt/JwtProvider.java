package com.example.english.common.jwt;

import com.example.english.common.enums.Role;
import com.example.english.domain.db.entity.User;
import com.example.english.domain.db.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    private final UserRepository userRepository;

    public JwtProvider(@Value("${jwt.secret}") String secret, UserRepository userRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userRepository = userRepository;
    }

    public JwtDto generateToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        Date accessExpired = new Date(now + 1000 * 60 * 60 * 24);
        Date refreshExpired = new Date(now + 1000 * 60 * 60 * 24 * 7);

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("유저를 찾지 못했습니다."));

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        String accessToken = Jwts.builder()
                .setHeader(headers)
                .setSubject(user.getId().toString())
                .claim("auth", authorities)
                .setExpiration(accessExpired)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeader(headers)
                .setSubject(user.getId().toString())
                .claim("auth", authorities)
                .setExpiration(refreshExpired)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseToken(accessToken);

        if(claims.get("auth") == null) {
            throw new RuntimeException("권한이 없는 토큰");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User user = User.builder()
                .id(Long.valueOf(claims.getSubject()))
                .role(Role.valueOf(claims.get("auth").toString()))
                .build();

        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    public boolean isExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean validateToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public JwtDto generateRefresh(Authentication authentication) {
        User entity = (User) authentication.getPrincipal();
        log.info("id: {}", entity.getId());

        User user = userRepository.findById(entity.getId()).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), null, user.getAuthorities());
        return generateToken(authenticationToken);
    }

    public Claims parseToken(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
