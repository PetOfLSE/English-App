package com.example.english.common.custom;

import com.example.english.domain.db.entity.User;
import com.example.english.domain.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       log.info("username : {}", username);
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + "를 찾지 못했습니다"));

        return user;
    }
}
