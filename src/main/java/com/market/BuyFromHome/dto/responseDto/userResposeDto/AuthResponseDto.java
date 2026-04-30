package com.market.BuyFromHome.dto.responseDto.userResposeDto;


import com.market.BuyFromHome.enums.AuthProvider;
import com.market.BuyFromHome.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private String token;
    private Long id;

    private String firstName;
    private String lastName;

    private String email;
    private String phoneNumber;

    private Role role;
    private AuthProvider provider;

    private boolean enabled;
}
