package com.market.BuyFromHome.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.market.BuyFromHome.enums.AuthProvider;
import com.market.BuyFromHome.enums.Role;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.GoogleAuthRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserLoginRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserRegisterRequest;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.UserRepository;
import com.market.BuyFromHome.security.JwtUtil;
import jakarta.transaction.Transactional;
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
    private final GoogleAuthService googleAuthService;

    @Value("${google.client.id}")
    private String googleClientId;


    @Transactional
    public AuthResponseDto register(UserRegisterRequest requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException(
                    "Email already registered: " + requestDto.getEmail());
        }


        User user = User.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .phoneNumber(requestDto.getPhoneNumber())
                .provider(AuthProvider.LOCAL)
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(requestDto.getPhoneNumber())
                .provider(user.getProvider())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }


    @Override
    public AuthResponseDto login(UserLoginRequest requestDto) {
        return null;
    }



    @Override
    public AuthResponseDto googleRegister(GoogleAuthRequest requestDto) {

        GoogleIdToken.Payload payload =
                googleAuthService.verifyToken(requestDto.getIdToken());

        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new RuntimeException(
                    "Email already registered: " + payload.getEmail());
        }
        User user = User.builder()
                .firstName((String) payload.get("given_name"))
                .lastName((String) payload.get("family_name"))
                .email(payload.getEmail())
                .googleId(payload.getSubject())
                .provider(AuthProvider.GOOGLE)
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .provider(user.getProvider())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }

}
