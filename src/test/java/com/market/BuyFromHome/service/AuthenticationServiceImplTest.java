package com.market.BuyFromHome.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import java.util.Optional;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.GoogleAuthRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserLoginRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserRegisterRequest;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import com.market.BuyFromHome.enums.AuthProvider;
import com.market.BuyFromHome.enums.Role;
import com.market.BuyFromHome.exception.AppException;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @Mock
    GoogleAuthService googleAuthService;

    // ==========================
    // LOCAL REGISTRATION TESTS
    // ==========================


    @Test
    @DisplayName("Should register user successfully")
    void localRegisterUserSuccessfully() {

        UserRegisterRequest registerRequest = new UserRegisterRequest(
                "Caleb",
                "Ezak",
                "caleb@test.com",
                "password1234",
                "+2348079921348"
        );

        when(userRepository.existsByEmail("caleb@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password1234"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);   // Simulate JPA assigning an ID
                    return user;
                });

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        AuthResponseDto responseDto =
                authenticationServiceImpl.localRegister(registerRequest);

        assertThat(responseDto.getId()).isEqualTo(1L);
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

        assertThatThrownBy(() ->
                authenticationServiceImpl.localRegister(registerRequest))
                .isInstanceOf(AppException.class)
                .hasMessage("Email already registered.")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }


//    @Test
//    @DisplayName("Should throw exception when email is null or empty")
//    void shouldThrowExceptionWhenEmailIsNullOrEmpty() {
//
//        UserRegisterRequest registerRequest = new UserRegisterRequest(
//                "Caleb", "Ezak", null,
//                "password1234", "+2348079921348"
//        );
//
//        assertThatThrownBy(() -> authenticationServiceImpl.register(registerRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Email cannot be null or empty");
//    }

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

        authenticationServiceImpl.localRegister(registerRequest);

        verify(passwordEncoder).encode("password1234");
    }

//    @Test
//    @DisplayName("Should throw exception when password is empty")
//    void shouldThrowExceptionWhenPasswordIsEmptyOrNull() {
//
//        UserRegisterRequest registerRequest = new UserRegisterRequest(
//                "Caleb", "Ezak", "caleb@test.com",
//                "", "+2348079921348"
//        );
//
//        assertThatThrownBy(() -> authenticationServiceImpl.register(registerRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Password cannot be empty or null");
//    }
//
//    @Test
//    @DisplayName("Should throw exception when password contains spaces")
//    void throwPasswordContainsSpaces() {
//
//        UserRegisterRequest registerRequest = new UserRegisterRequest(
//                "Caleb", "Ezak", "caleb@test.com",
//                "password 123", "+2348079921348"
//        );
//
//        assertThatThrownBy(() -> authenticationServiceImpl.register(registerRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Password cannot contain spaces");
//    }
//
//    @Test
//    @DisplayName("Should throw exception when password contains only spaces")
//    void throwPasswordContainsOnlySpaces() {
//
//        UserRegisterRequest registerRequest = new UserRegisterRequest(
//                "Caleb", "Ezak", "caleb@test.com",
//                "         ", "+2348079921348"
//        );
//
//        assertThatThrownBy(() -> authenticationServiceImpl.register(registerRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Password cannot be empty or null");
//    }

//    @Test
//    @DisplayName("Password should throw exception when it has less then 9 character")
//    void throwPasswordLessThan9Characters() {
//
//        UserRegisterRequest registerRequest = new UserRegisterRequest(
//                "Caleb", "Ezak", "Caleb@test.com",
//                "password1234", "+2348079921348"
//        );
//
//        assertThatThrownBy(() -> authenticationServiceImpl.register(registerRequest))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Password must contain at least 9 characters");
//    }


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

        authenticationServiceImpl.localRegister(registerRequest);

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

    @Test
    @DisplayName("Should generate JWT token after successful registration")
    void shouldGenerateJwtTokenAfterRegistration() {

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

        authenticationServiceImpl.localRegister(registerRequest);

        verify(jwtUtil).generateToken(
                "caleb@test.com",
                Role.CUSTOMER.name()
        );
    }

    // ==========================
    // GOOGLE REGISTRATION TESTS
    // ==========================

    @Test
    @DisplayName("Should register Google user successfully")
    void localRegisterGoogleUserSuccessfully() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");
        when(payload.getSubject()).thenReturn("google123");
        when(payload.get("given_name")).thenReturn("Caleb");
        when(payload.get("family_name")).thenReturn("Ezak");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        when(userRepository.existsByEmail("caleb@test.com"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        AuthResponseDto response =
                authenticationServiceImpl.googleRegister(request);

        assertThat(response.getFirstName()).isEqualTo("Caleb");
        assertThat(response.getLastName()).isEqualTo("Ezak");
        assertThat(response.getEmail()).isEqualTo("caleb@test.com");
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when Google email already exists")
    void throwGoogleEmailAlreadyExists() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        when(userRepository.existsByEmail("caleb@test.com"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                authenticationServiceImpl.googleRegister(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Email already registered")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }
    @Test
    @DisplayName("Should verify Google token")
    void shouldVerifyGoogleToken() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");
        when(payload.getSubject()).thenReturn("google123");
        when(payload.get("given_name")).thenReturn("Caleb");
        when(payload.get("family_name")).thenReturn("Ezak");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        authenticationServiceImpl.googleRegister(request);

        verify(googleAuthService).verifyToken("mock-id-token");
    }

    @Test
    @DisplayName("Should generate JWT for Google user")
    void shouldGenerateJwtForGoogleUser() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");
        when(payload.getSubject()).thenReturn("google123");
        when(payload.get("given_name")).thenReturn("Caleb");
        when(payload.get("family_name")).thenReturn("Ezak");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        authenticationServiceImpl.googleRegister(request);

        verify(jwtUtil)
                .generateToken("caleb@test.com", "CUSTOMER");
    }

    @Test
    @DisplayName("Should not encode password for Google registration")
    void shouldNotEncodePasswordForGoogleRegistration() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");
        when(payload.getSubject()).thenReturn("google123");
        when(payload.get("given_name")).thenReturn("Caleb");
        when(payload.get("family_name")).thenReturn("Ezak");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        authenticationServiceImpl.googleRegister(request);

        verify(passwordEncoder, never()).encode(anyString());
    }


    // ==========================
    // LOCAL LOGIN TESTS
    // ==========================
    @Test
    @DisplayName("Should login successfully")
    void loginSuccessfully() {

        UserLoginRequest request = new UserLoginRequest(
                "caleb@test.com",
                "password1234"
        );

        User user = User.builder()
                .email("caleb@test.com")
                .password("hashedPassword")
                .role(Role.CUSTOMER)
                .enabled(true)
                .provider(AuthProvider.LOCAL)
                .build();

        when(userRepository.findByEmail("caleb@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password1234", "hashedPassword"))
                .thenReturn(true);

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        AuthResponseDto response =
                authenticationServiceImpl.localLogin(request);

        assertThat(response.getEmail()).isEqualTo("caleb@test.com");
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
    }

    @Test
    @DisplayName("Should throw exception when email does not exist")
    void throwEmailDoesNotExist() {

        UserLoginRequest request = new UserLoginRequest(
                "caleb@test.com",
                "password1234"
        );

        when(userRepository.findByEmail("caleb@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authenticationServiceImpl.localLogin(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Invalid email or password")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should throw exception when account is disabled")
    void throwAccountDisabled() {

        UserLoginRequest request = new UserLoginRequest(
                "caleb@test.com",
                "password1234"
        );

        User user = User.builder()
                .email("caleb@test.com")
                .password("hashedPassword")
                .provider(AuthProvider.LOCAL)
                .enabled(false)
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("caleb@test.com"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                authenticationServiceImpl.localLogin(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Account is disabled")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }


    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void throwIncorrectPassword() {

        UserLoginRequest request = new UserLoginRequest(
                "caleb@test.com",
                "password1234"
        );

        User user = User.builder()
                .email("caleb@test.com")
                .password("hashedPassword")
                .provider(AuthProvider.LOCAL)
                .enabled(true)
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("caleb@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password1234",
                "hashedPassword"))
                .thenReturn(false);

        assertThatThrownBy(() ->
                authenticationServiceImpl.localLogin(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Invalid email or password")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    @DisplayName("Should throw exception when Google user logs in locally")
    void throwGoogleLoginWithLocalLogin() {

        UserLoginRequest request = new UserLoginRequest(
                "caleb@test.com",
                "password1234"
        );

        User user = User.builder()
                .email("caleb@test.com")
                .provider(AuthProvider.GOOGLE)
                .enabled(true)
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("caleb@test.com"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                authenticationServiceImpl.localLogin(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Please sign in with Google")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ==========================
    // GOOGLE LOGIN TESTS
    // ==========================

    @Test
    @DisplayName("Should login Google user successfully")
    void loginGoogleUserSuccessfully() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        User user = User.builder()
                .firstName("Caleb")
                .lastName("Ezak")
                .email("caleb@test.com")
                .googleId("google123")
                .provider(AuthProvider.GOOGLE)
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        when(userRepository.findByEmail("caleb@test.com"))
                .thenReturn(java.util.Optional.of(user));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        AuthResponseDto response =
                authenticationServiceImpl.googleLogin(request);

        assertThat(response.getEmail()).isEqualTo("caleb@test.com");
        assertThat(response.getProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
    }

    @Test
    @DisplayName("Should throw exception when Google account is not registered")
    void throwGoogleAccountNotRegistered() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        when(userRepository.findByEmail("caleb@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authenticationServiceImpl.googleLogin(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Google account is not registered")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @DisplayName("Should throw exception when Google account is disabled")
    void throwDisabledGoogleAccount() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        User user = User.builder()
                .email("caleb@test.com")
                .provider(AuthProvider.GOOGLE)
                .role(Role.CUSTOMER)
                .enabled(false)
                .build();

        when(userRepository.findByEmail("caleb@test.com"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                authenticationServiceImpl.googleLogin(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Account is disabled")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }


    @Test
    @DisplayName("Should verify Google token during login")
    void shouldVerifyGoogleTokenDuringLogin() {

        GoogleAuthRequest request = new GoogleAuthRequest();
        request.setIdToken("mock-id-token");

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail()).thenReturn("caleb@test.com");

        when(googleAuthService.verifyToken("mock-id-token"))
                .thenReturn(payload);

        User user = User.builder()
                .email("caleb@test.com")
                .provider(AuthProvider.GOOGLE)
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(java.util.Optional.of(user));

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        authenticationServiceImpl.googleLogin(request);

        verify(googleAuthService).verifyToken("mock-id-token");
    }

}


