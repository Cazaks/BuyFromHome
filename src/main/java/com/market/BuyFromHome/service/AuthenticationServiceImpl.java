package com.market.BuyFromHome.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.*;
import com.market.BuyFromHome.enums.AuthProvider;
import com.market.BuyFromHome.enums.Role;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.UserRepository;
import com.market.BuyFromHome.security.JwtUtil;
import org.springframework.http.HttpStatus;
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
    private final EmailService emailService;

    @Value("${google.client.id}")
    private String googleClientId;


    // ==========================
    // LOCAL REGISTRATION METHOD IMPLEMENTATION
    // ==========================
    @Transactional
    @Override
    public AuthResponseDto localRegister(UserRegisterRequest requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new AppException(
                    "Email already registered.",
                    HttpStatus.CONFLICT
            );
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

        return mapToAuthResponse(savedUser, token);
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
            throw new AppException(
                    "Email already registered.",
                    HttpStatus.CONFLICT
            );
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

        return mapToAuthResponse(savedUser, token);
    }

    // ==========================
    // LOCAL LOGIN METHOD IMPLEMENTATION
    // ==========================
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto localLogin(UserLoginRequest requestDto) {

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() ->
                        new AppException(
                                "Invalid email or password.",
                                HttpStatus.UNAUTHORIZED
                        ));

        if (user.getProvider() == AuthProvider.GOOGLE) {
            throw new AppException(
                    "Please sign in with Google.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!user.isEnabled()) {
            throw new AppException(
                    "Account is disabled.",
                    HttpStatus.FORBIDDEN
            );
        }

        if (!passwordEncoder.matches(
                requestDto.getPassword(),
                user.getPassword())) {

            throw new AppException(
                    "Invalid email or password.",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());

        return mapToAuthResponse(user, token);
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
                        new AppException(
                                "Google account is not registered.",
                                HttpStatus.NOT_FOUND
                        ));

        // Link the Google account if this is the first Google login
        if (user.getGoogleId() == null) {
            user.setGoogleId(payload.getSubject());
            userRepository.save(user);
        }

        if (!user.isEnabled()) {
            throw new AppException(
                    "Account is disabled.",
                    HttpStatus.FORBIDDEN
            );
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());

        return mapToAuthResponse(user, token);
    }


    @Transactional
    @Override
    public void forgotPassword(ForgotPasswordRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new AppException(
                        "No account found with that email.",
                        HttpStatus.NOT_FOUND
                ));

        if (user.getProvider() == AuthProvider.GOOGLE) {
            throw new AppException(
                    "This account uses Google sign-in. Password reset isn't available.",
                    HttpStatus.BAD_REQUEST
            );
        }

        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest requestDto) {
        User user = userRepository.findByResetToken(requestDto.getToken())
                .orElseThrow(() -> new AppException(
                        "Invalid or expired reset link.",
                        HttpStatus.BAD_REQUEST
                ));

        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new AppException(
                    "Invalid or expired reset link.",
                    HttpStatus.BAD_REQUEST
            );
        }

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }


    private AuthResponseDto mapToAuthResponse(User user, String token) {

        return AuthResponseDto.builder()
                .id(user.getUserId())
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .provider(user.getProvider())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }
}
