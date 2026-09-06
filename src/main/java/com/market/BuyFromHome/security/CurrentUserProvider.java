package com.market.BuyFromHome.security;

import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Authenticated user not found.",
                        HttpStatus.UNAUTHORIZED
                ));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }
}