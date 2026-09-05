package com.market.BuyFromHome.service;

import com.google.common.base.Optional;
import com.market.BuyFromHome.dto.requestDto.userRequestDto.*;
import com.market.BuyFromHome.dto.responseDto.userResposeDto.AuthResponseDto;
import com.market.BuyFromHome.model.User;
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

    @Transactional
    void forgotPassword(ForgotPasswordRequest requestDto);

    @Transactional
    void resetPassword(ResetPasswordRequest requestDto);

}
