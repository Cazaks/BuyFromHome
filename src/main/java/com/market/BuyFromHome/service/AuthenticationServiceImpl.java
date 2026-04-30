package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.userRequestDto.GoogleAuthRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserLoginRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserRegisterRequest;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import com.market.BuyFromHome.repository.UserRepository;
import com.market.BuyFromHome.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client.id}")
    private String googleClientId;

    // ───── Register — TDD Red stage, returns null intentionally ───────────

    @Override
    public AuthResponseDto register(UserRegisterRequest requestDto) {
        return null;
    }

    // ───── Login — TDD Red stage, returns null intentionally ─────────────

    @Override
    public AuthResponseDto login(UserLoginRequest requestDto) {
        return null;
    }

    // ───── Google Auth — TDD Red stage, returns null intentionally ────────

    @Override
    public AuthResponseDto googleAuth(GoogleAuthRequest requestDto) {
        return null;
    }

}
