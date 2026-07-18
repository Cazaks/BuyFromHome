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
import org.springframework.transaction.annotation.Transactional;
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


    // ==========================
    // LOCAL REGISTRATION METHOD IMPLEMENTATION
    // ==========================
    @Transactional
    @Override
    public AuthResponseDto localRegister(UserRegisterRequest requestDto) {

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

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return AuthResponseDto.builder()
                .id(savedUser.getId())
                .token(token)
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .phoneNumber(savedUser.getPhoneNumber())
                .provider(savedUser.getProvider())
                .role(savedUser.getRole())
                .enabled(savedUser.isEnabled())
                .build();
    }

    // ==========================
    // GOOGLE REGISTRATION METHOD IMPLEMENTATION
    // ==========================
    @Transactional
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

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return AuthResponseDto.builder()
                .id(savedUser.getId())
                .token(token)
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .provider(savedUser.getProvider())
                .role(savedUser.getRole())
                .enabled(savedUser.isEnabled())
                .build();
    }

    // ==========================
    // LOCAL LOGIN METHOD IMPLEMENTATION
    // ==========================
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto localLogin(UserLoginRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (user.getProvider() == AuthProvider.GOOGLE) {
            throw new RuntimeException("Please sign in with Google");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        if (!passwordEncoder.matches(
                requestDto.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());

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

    // ==========================
    // GOOGLE LOGIN METHOD IMPLEMENTATION
    // ==========================
    @Transactional
    @Override
    public AuthResponseDto googleLogin(GoogleAuthRequest requestDto) {

        GoogleIdToken.Payload payload =
                googleAuthService.verifyToken(requestDto.getIdToken());

        User user = userRepository.findByEmail(payload.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Google account is not registered"));

        // Link the Google account if this is the first Google login
        if (user.getGoogleId() == null) {
            user.setGoogleId(payload.getSubject());
            userRepository.save(user);
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());

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
