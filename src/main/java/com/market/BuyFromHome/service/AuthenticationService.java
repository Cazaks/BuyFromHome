package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.userRequestDto.GoogleAuthRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserLoginRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserRegisterRequest;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import org.springframework.transaction.annotation.Transactional;

public interface AuthenticationService {
    AuthResponseDto localRegister(UserRegisterRequest requestDto);

    AuthResponseDto localLogin(UserLoginRequest requestDto);

    AuthResponseDto googleRegister(GoogleAuthRequest requestDto);

    // ==========================
    // GOOGLE LOGIN METHOD IMPLEMENTATION
    // ==========================
    @Transactional(readOnly = true)
    AuthResponseDto googleLogin(GoogleAuthRequest requestDto);
}
