package com.market.BuyFromHome.config;

import com.market.BuyFromHome.enums.AuthProvider;
import com.market.BuyFromHome.enums.Role;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSetUp {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdminProfile() {

        String adminEmail = "admin@buyfromhome.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin account already exists.");
            return;
        }

        User admin = User.builder()
                .firstName("Super")
                .lastName("Admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("Admin@12345"))
                .phoneNumber("+2348000000000")
                .provider(AuthProvider.LOCAL)
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userRepository.save(admin);

        log.info("======================================");
        log.info("Admin account created successfully");
        log.info("Email: {}", adminEmail);
        log.info("Default Password: Admin@12345");
        log.info("======================================");
    }
}