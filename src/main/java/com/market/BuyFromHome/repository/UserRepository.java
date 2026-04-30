package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.enums.Role;
import com.market.BuyFromHome.model.User;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Used in login() — check if email exists before password check
    boolean existsByEmail(String email);

    // Used in googleAuth() — find by email and provider
    Optional<User> findByEmailAndProvider(String email, String provider);

    // Used in admin features later — find all customers
    List<User> findAllByRole(Role role);
}
