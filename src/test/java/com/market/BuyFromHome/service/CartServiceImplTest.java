package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.cartItemRequest.CartItemRequestDto;
import com.market.BuyFromHome.dto.responseDto.cartResponse.CartResponseDto;
import com.market.BuyFromHome.enums.CartStatus;
import com.market.BuyFromHome.enums.MeasurementUnit;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.*;
import com.market.BuyFromHome.repository.CartItemRepository;
import com.market.BuyFromHome.repository.CartRepository;
import com.market.BuyFromHome.repository.ProductSellingMeasurementRepository;
import com.market.BuyFromHome.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductSellingMeasurementRepository productSellingMeasurementRepository;

    @Mock
    private UserRepository userRepository;

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
    @DisplayName("Should return empty cart when user does not have a persistent cart")
    void shouldReturnEmptyCartWhenUserDoesNotHavePersistentCart() {

        User user = buildUser();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.empty());

        CartResponseDto response =
                cartServiceImpl.getCart(user.getUserId());

        assertThat(response).isNotNull();

        assertThat(response.getCartId())
                .isNull();

        assertThat(response.getUserId())
                .isEqualTo(user.getUserId());

        assertThat(response.getStatus())
                .isEqualTo(CartStatus.ACTIVE);

        assertThat(response.getItems())
                .isEmpty();

        assertThat(response.getTotalAmount())
                .isEqualByComparingTo(BigDecimal.ZERO);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());
    }

    @Test
    @DisplayName("Should add new item to cart successfully")
    void shouldAddNewItemToCartSuccessfully() {

        User user = buildUser();

        Cart cart = buildCart(user);

        ProductSellingMeasurement sellingMeasurement =
                buildSellingMeasurement();

        CartItemRequestDto requestDto =
                CartItemRequestDto.builder()
                        .sellingMeasurementId(
                                sellingMeasurement.getSellingMeasurementId()
                        )
                        .quantity(2)
                        .build();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(productSellingMeasurementRepository.findById(
                sellingMeasurement.getSellingMeasurementId()
        ))
                .thenReturn(Optional.of(sellingMeasurement));

        when(cartItemRepository
                .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                        cart.getCartId(),
                        sellingMeasurement.getSellingMeasurementId()
                ))
                .thenReturn(Optional.empty());

        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CartResponseDto response =
                cartServiceImpl.addItem(
                        user.getUserId(),
                        requestDto
                );

        assertThat(response).isNotNull();

        assertThat(cart.getItems())
                .hasSize(1);

        CartItem cartItem =
                cart.getItems().get(0);

        assertThat(cartItem.getSellingMeasurement())
                .isEqualTo(sellingMeasurement);

        assertThat(cartItem.getQuantity())
                .isEqualTo(2);

        assertThat(cartItem.getPriceAtTimeOfAdding())
                .isEqualByComparingTo(
                        sellingMeasurement.getSellingPrice()
                );

        verify(userRepository)
                .findById(user.getUserId());

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(productSellingMeasurementRepository)
                .findById(
                        sellingMeasurement.getSellingMeasurementId()
                );

        verify(cartItemRepository)
                .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                        cart.getCartId(),
                        sellingMeasurement.getSellingMeasurementId()
                );

        verify(cartItemRepository)
                .save(any(CartItem.class));
    }


    @Test
    @DisplayName("Should create cart and add first item when user does not have a cart")
    void shouldCreateCartAndAddFirstItemWhenUserDoesNotHaveCart() {

        User user = buildUser();

        ProductSellingMeasurement sellingMeasurement =
                buildSellingMeasurement();

        CartItemRequestDto requestDto =
                CartItemRequestDto.builder()
                        .sellingMeasurementId(
                                sellingMeasurement.getSellingMeasurementId()
                        )
                        .quantity(2)
                        .build();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.empty());

        when(productSellingMeasurementRepository.findById(
                sellingMeasurement.getSellingMeasurementId()
        ))
                .thenReturn(Optional.of(sellingMeasurement));

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> {

                    Cart cart = invocation.getArgument(0);
                    cart.setCartId(1L);
                    return cart;
                });

        when(cartItemRepository
                .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                        anyLong(),
                        eq(sellingMeasurement.getSellingMeasurementId())
                ))
                .thenReturn(Optional.empty());

        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CartResponseDto response =
                cartServiceImpl.addItem(
                        user.getUserId(),
                        requestDto
                );

        assertThat(response).isNotNull();

        assertThat(response.getUserId())
                .isEqualTo(user.getUserId());

        assertThat(response.getStatus())
                .isEqualTo(CartStatus.ACTIVE);

        assertThat(response.getItems())
                .hasSize(1);

        assertThat(response.getItems().get(0).getQuantity())
                .isEqualTo(2);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(cartRepository)
                .save(any(Cart.class));

        verify(cartItemRepository)
                .save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should increase quantity when selling measurement already exists in cart")
    void shouldIncreaseQuantityWhenSellingMeasurementAlreadyExistsInCart() {

        User user = buildUser();

        Cart cart = buildCart(user);

        ProductSellingMeasurement measurement =
                buildSellingMeasurement();

        CartItem existingItem =
                CartItem.builder()
                        .cart(cart)
                        .sellingMeasurement(measurement)
                        .quantity(2)
                        .priceAtTimeOfAdding(
                                measurement.getSellingPrice()
                        )
                        .build();

        cart.getItems().add(existingItem);

        CartItemRequestDto request =
                CartItemRequestDto.builder()
                        .sellingMeasurementId(
                                measurement.getSellingMeasurementId()
                        )
                        .quantity(3)
                        .build();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(productSellingMeasurementRepository.findById(
                measurement.getSellingMeasurementId()))
                .thenReturn(Optional.of(measurement));

        when(cartItemRepository
                .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                        cart.getCartId(),
                        measurement.getSellingMeasurementId()))
                .thenReturn(Optional.of(existingItem));

        CartResponseDto response =
                cartServiceImpl.addItem(
                        user.getUserId(),
                        request
                );

        assertThat(response).isNotNull();

        assertThat(existingItem.getQuantity())
                .isEqualTo(5);

        verify(userRepository)
                .findById(user.getUserId());

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(productSellingMeasurementRepository)
                .findById(
                        measurement.getSellingMeasurementId()
                );

        verify(cartItemRepository)
                .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                        cart.getCartId(),
                        measurement.getSellingMeasurementId()
                );

        verify(cartItemRepository)
                .save(existingItem);
    }

    @Test
    @DisplayName("Should throw exception when selling measurement does not exist")
    void shouldThrowExceptionWhenSellingMeasurementDoesNotExist() {

        User user = buildUser();

        Cart cart = buildCart(user);

        CartItemRequestDto requestDto =
                CartItemRequestDto.builder()
                        .sellingMeasurementId(99L)
                        .quantity(2)
                        .build();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(productSellingMeasurementRepository.findById(99L))
                .thenReturn(Optional.empty());

        AppException exception =
                assertThrows(
                        AppException.class,
                        () -> cartServiceImpl.addItem(
                                user.getUserId(),
                                requestDto
                        )
                );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository)
                .findById(user.getUserId());

        verify(productSellingMeasurementRepository)
                .findById(99L);

        verify(cartRepository, never())
                .findByUser_UserId(anyLong());

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }


    @Test
    @DisplayName("Should throw exception when selling measurement is disabled")
    void shouldThrowExceptionWhenSellingMeasurementIsDisabled() {

        User user = buildUser();

        Cart cart = buildCart(user);

        ProductSellingMeasurement measurement =
                buildSellingMeasurement();

        measurement.setEnabled(false);

        CartItemRequestDto request =
                CartItemRequestDto.builder()
                        .sellingMeasurementId(
                                measurement.getSellingMeasurementId()
                        )
                        .quantity(2)
                        .build();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(productSellingMeasurementRepository.findById(
                measurement.getSellingMeasurementId()))
                .thenReturn(Optional.of(measurement));

        AppException exception =
                assertThrows(
                        AppException.class,
                        () -> cartServiceImpl.addItem(
                                user.getUserId(),
                                request
                        )
                );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement is disabled.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository)
                .findById(user.getUserId());

        verify(productSellingMeasurementRepository)
                .findById(
                        measurement.getSellingMeasurementId()
                );

        verify(cartRepository, never())
                .findByUser_UserId(anyLong());

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw exception when requested quantity exceeds available stock")
    void shouldThrowExceptionWhenQuantityExceedsStock() {

        User user = buildUser();

        Cart cart = buildCart(user);

        ProductSellingMeasurement measurement =
                buildSellingMeasurement();

        measurement.setQuantityInStock(5);

        CartItemRequestDto request =
                CartItemRequestDto.builder()
                        .sellingMeasurementId(
                                measurement.getSellingMeasurementId()
                        )
                        .quantity(6)
                        .build();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(productSellingMeasurementRepository.findById(
                measurement.getSellingMeasurementId()))
                .thenReturn(Optional.of(measurement));

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        AppException exception =
                assertThrows(
                        AppException.class,
                        () -> cartServiceImpl.addItem(
                                user.getUserId(),
                                request
                        )
                );

        assertThat(exception.getMessage())
                .isEqualTo("Insufficient stock.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository)
                .findById(user.getUserId());

        verify(productSellingMeasurementRepository)
                .findById(
                        measurement.getSellingMeasurementId()
                );

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw exception when updated cart quantity exceeds available stock")
    void shouldThrowExceptionWhenUpdatedQuantityExceedsStock() {

        User user = buildUser();

        Cart cart = buildCart(user);

        ProductSellingMeasurement measurement =
                buildSellingMeasurement();

        measurement.setQuantityInStock(5);

        CartItem existingItem =
                CartItem.builder()
                        .cart(cart)
                        .sellingMeasurement(measurement)
                        .quantity(3)
                        .priceAtTimeOfAdding(
                                measurement.getSellingPrice()
                        )
                        .build();

        cart.getItems().add(existingItem);

        CartItemRequestDto request =
                CartItemRequestDto.builder()
                        .sellingMeasurementId(
                                measurement.getSellingMeasurementId()
                        )
                        .quantity(3)
                        .build();

        when(userRepository.findById(user.getUserId()))
                .thenReturn(Optional.of(user));

        when(productSellingMeasurementRepository.findById(
                measurement.getSellingMeasurementId()))
                .thenReturn(Optional.of(measurement));

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository
                .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                        cart.getCartId(),
                        measurement.getSellingMeasurementId()))
                .thenReturn(Optional.of(existingItem));

        AppException exception =
                assertThrows(
                        AppException.class,
                        () -> cartServiceImpl.addItem(
                                user.getUserId(),
                                request
                        )
                );

        assertThat(exception.getMessage())
                .isEqualTo("Insufficient stock.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(existingItem.getQuantity())
                .isEqualTo(3);

        verify(userRepository)
                .findById(user.getUserId());

        verify(productSellingMeasurementRepository)
                .findById(
                        measurement.getSellingMeasurementId()
                );

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(cartItemRepository)
                .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                        cart.getCartId(),
                        measurement.getSellingMeasurementId()
                );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should update cart item quantity successfully")
    void shouldUpdateCartItemQuantitySuccessfully() {

        User user = buildUser();
        Cart cart = buildCart(user);

        ProductSellingMeasurement sellingMeasurement =
                buildSellingMeasurement();

        CartItem cartItem =
                CartItem.builder()
                        .cartItemId(1L)
                        .cart(cart)
                        .sellingMeasurement(sellingMeasurement)
                        .quantity(2)
                        .priceAtTimeOfAdding(
                                sellingMeasurement.getSellingPrice()
                        )
                        .build();

        cart.getItems().add(cartItem);

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_CartIdAndCartItemId(
                cart.getCartId(),
                cartItem.getCartItemId()
        ))
                .thenReturn(Optional.of(cartItem));

        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CartResponseDto response =
                cartServiceImpl.updateItemQuantity(
                        user.getUserId(),
                        cartItem.getCartItemId(),
                        5
                );

        assertThat(response).isNotNull();

        assertThat(cartItem.getQuantity())
                .isEqualTo(5);

        assertThat(response.getItems())
                .hasSize(1);

        assertThat(response.getItems().get(0).getQuantity())
                .isEqualTo(5);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(cartItemRepository)
                .findByCart_CartIdAndCartItemId(
                        cart.getCartId(),
                        cartItem.getCartItemId()
                );

        verify(cartItemRepository)
                .save(cartItem);
    }

    @Test
    @DisplayName("Should throw exception when cart does not exist while updating item quantity")
    void shouldThrowExceptionWhenCartDoesNotExistWhileUpdatingItemQuantity() {

        User user = buildUser();

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> cartServiceImpl.updateItemQuantity(
                        user.getUserId(),
                        1L,
                        5
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Cart not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verifyNoInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Should throw exception when cart item does not exist in user's cart")
    void shouldThrowExceptionWhenCartItemDoesNotExistInUsersCart() {

        User user = buildUser();

        Cart cart = buildCart(user);

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_CartIdAndCartItemId(
                cart.getCartId(),
                1L
        ))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> cartServiceImpl.updateItemQuantity(
                        user.getUserId(),
                        1L,
                        5
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Cart item not found.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(cartItemRepository)
                .findByCart_CartIdAndCartItemId(
                        cart.getCartId(),
                        1L
                );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw exception when selling measurement is disabled while updating quantity")
    void shouldThrowExceptionWhenSellingMeasurementIsDisabledWhileUpdatingQuantity() {

        User user = buildUser();

        Cart cart = buildCart(user);

        ProductSellingMeasurement sellingMeasurement =
                buildSellingMeasurement();

        sellingMeasurement.setEnabled(false);

        CartItem cartItem =
                CartItem.builder()
                        .cart(cart)
                        .sellingMeasurement(sellingMeasurement)
                        .quantity(2)
                        .priceAtTimeOfAdding(
                                sellingMeasurement.getSellingPrice()
                        )
                        .build();

        cart.getItems().add(cartItem);

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_CartIdAndCartItemId(
                cart.getCartId(),
                cartItem.getCartItemId()
        ))
                .thenReturn(Optional.of(cartItem));

        AppException exception = assertThrows(
                AppException.class,
                () -> cartServiceImpl.updateItemQuantity(
                        user.getUserId(),
                        cartItem.getCartItemId(),
                        5
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Selling measurement is disabled.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(cartItem.getQuantity())
                .isEqualTo(2);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(cartItemRepository)
                .findByCart_CartIdAndCartItemId(
                        cart.getCartId(),
                        cartItem.getCartItemId()
                );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw exception when updated quantity exceeds available stock")
    void shouldThrowExceptionWhenUpdatedQuantityExceedsAvailableStock() {

        User user = buildUser();

        Cart cart = buildCart(user);

        ProductSellingMeasurement sellingMeasurement =
                buildSellingMeasurement();

        sellingMeasurement.setQuantityInStock(5);

        CartItem cartItem =
                CartItem.builder()
                        .cart(cart)
                        .sellingMeasurement(sellingMeasurement)
                        .quantity(2)
                        .priceAtTimeOfAdding(
                                sellingMeasurement.getSellingPrice()
                        )
                        .build();

        cart.getItems().add(cartItem);

        when(cartRepository.findByUser_UserId(user.getUserId()))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_CartIdAndCartItemId(
                cart.getCartId(),
                cartItem.getCartItemId()
        ))
                .thenReturn(Optional.of(cartItem));

        AppException exception = assertThrows(
                AppException.class,
                () -> cartServiceImpl.updateItemQuantity(
                        user.getUserId(),
                        cartItem.getCartItemId(),
                        6
                )
        );

        assertThat(exception.getMessage())
                .isEqualTo("Insufficient stock.");

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(cartItem.getQuantity())
                .isEqualTo(2);

        verify(cartRepository)
                .findByUser_UserId(user.getUserId());

        verify(cartItemRepository)
                .findByCart_CartIdAndCartItemId(
                        cart.getCartId(),
                        cartItem.getCartItemId()
                );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
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

    private ProductSellingMeasurement buildSellingMeasurement() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .category(category)
                .build();

        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .productVariety("Local Rice")
                .productSpecification("Short Grain")
                .build();

        return ProductSellingMeasurement.builder()
                .sellingMeasurementId(1L)
                .productOption(productOption)
                .measurementUnit(MeasurementUnit.BAG)
                .sellingPrice(new BigDecimal("8000.00"))
                .quantityInStock(20)
                .enabled(true)
                .build();
    }

}