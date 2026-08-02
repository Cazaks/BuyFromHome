package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.responseDto.cartResponse.CartResponseDto;
import com.market.BuyFromHome.enums.CartStatus;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Cart;
import com.market.BuyFromHome.model.User;
import com.market.BuyFromHome.repository.CartItemRepository;
import com.market.BuyFromHome.repository.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;


    @Test
    @DisplayName("Should get user's cart successfully")
    void shouldGetUsersCartSuccessfully() {

        User user = buildUser();

        Cart cart =
                buildCart(user);

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        CartResponseDto response =
                cartServiceImpl.getCart(user.getUserId());

        assertThat(response).isNotNull();

        assertThat(response.getCartId())
                .isEqualTo(cart.getCartId());

        assertThat(response.getUserId())
                .isEqualTo(user.getUserId());

        assertThat(response.getStatus())
                .isEqualTo(CartStatus.ACTIVE);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());
    }

    @Test
    @DisplayName("Should throw exception when user's cart does not exist")
    void shouldThrowExceptionWhenCartDoesNotExist() {

        User user = buildUser();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> cartServiceImpl.getCart(user.getUserId())
        );

        assertThat(exception.getMessage())
                .isEqualTo("Cart not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());
    }


    private User buildUser() {

        return User.builder()
                .userId(1L)
                .build();
    }

    private Cart buildCart(User user) {

        return Cart.builder()
                .cartId(1L)
                .user(user)
                .status(CartStatus.ACTIVE)
                .items(new ArrayList<>())
                .build();
    }

}