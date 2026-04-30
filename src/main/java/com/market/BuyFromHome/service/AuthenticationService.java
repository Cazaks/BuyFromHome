package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.userRequestDto.GoogleAuthRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserLoginRequest;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.UserRegisterRequest;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;

public interface AuthenticationService {
    AuthResponseDto register(UserRegisterRequest requestDto);

    AuthResponseDto login(UserLoginRequest requestDto);

    AuthResponseDto googleAuth(GoogleAuthRequest requestDto);
}
