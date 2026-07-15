package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserRegisterRequest;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import com.market.BuyFromHome.enums.AuthProvider;
import com.market.BuyFromHome.enums.Role;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.UserRepository;
import com.market.BuyFromHome.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthenticationServiceImpl authenticationServiceImpl;

    @Test
    @DisplayName("Should register user successfully")
    void registerUserSuccessfully() {

        UserRegisterRequest registerRequest = new UserRegisterRequest(
                "Caleb", "Ezak", "caleb@test.com",
                "password1234", "+2348079921348");

        when(userRepository.existsByEmail("caleb@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password1234"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        AuthResponseDto responseDto =
                authenticationServiceImpl.register(registerRequest);

        assertThat(responseDto.getFirstName()).isEqualTo("Caleb");
        assertThat(responseDto.getLastName()).isEqualTo("Ezak");
        assertThat(responseDto.getEmail()).isEqualTo("caleb@test.com");
        assertThat(responseDto.getPhoneNumber()).isEqualTo("+2348079921348");
        assertThat(responseDto.getToken()).isEqualTo("mock.jwt.token");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void throwEmailAlreadyExists() {
        UserRegisterRequest registerRequest = new UserRegisterRequest(
                "Caleb", "Ezak", "caleb@test.com",
                "password1234", "+2348079921348"
        );
        when(userRepository.existsByEmail("caleb@test.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> authenticationServiceImpl.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("Should encode password before saving user")
    void shouldEncodePasswordBeforeSavingUser() {

        UserRegisterRequest registerRequest = new UserRegisterRequest(
                "Caleb", "Ezak", "caleb@test.com",
                "password1234", "+2348079921348"
        );

        when(userRepository.existsByEmail("caleb@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password1234"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        authenticationServiceImpl.register(registerRequest);

        verify(passwordEncoder).encode("password1234");
    }

    @Test
    @DisplayName("Should save user with correct details")
    void shouldSaveUserWithCorrectDetails() {

        UserRegisterRequest registerRequest = new UserRegisterRequest(
                "Caleb", "Ezak", "caleb@test.com",
                "password1234", "+2348079921348"
        );

        when(userRepository.existsByEmail("caleb@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password1234"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        authenticationServiceImpl.register(registerRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getFirstName()).isEqualTo("Caleb");
        assertThat(savedUser.getLastName()).isEqualTo("Ezak");
        assertThat(savedUser.getEmail()).isEqualTo("caleb@test.com");
        assertThat(savedUser.getPhoneNumber()).isEqualTo("+2348079921348");
        assertThat(savedUser.getPassword()).isEqualTo("hashedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(savedUser.getProvider()).isEqualTo(AuthProvider.LOCAL);
        assertThat(savedUser.isEnabled()).isTrue();
    }

    


}


