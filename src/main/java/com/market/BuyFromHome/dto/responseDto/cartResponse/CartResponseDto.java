package com.market.BuyFromHome.dto.responseDto.cartResponse;

import com.market.BuyFromHome.dto.responseDto.cartItemResponse.CartItemResponseDto;
import com.market.BuyFromHome.enums.CartStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {

    private Long cartId;

    private Long userId;

    private List<CartItemResponseDto> items;

    private CartStatus status;

    private BigDecimal totalAmount;
}
