package com.market.BuyFromHome.dto.requestDto.userRequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=\\S+$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{9,}$",
            message = "Password must be at least 9 characters, contain uppercase, lowercase, digit, special character and no spaces"
    )
    private String newPassword;
}
