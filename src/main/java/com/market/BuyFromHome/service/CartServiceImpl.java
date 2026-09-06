package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.cartItemRequest.CartItemRequestDto;
import com.market.BuyFromHome.dto.responseDto.cartItemResponse.CartItemResponseDto;
import com.market.BuyFromHome.dto.responseDto.cartResponse.CartResponseDto;
import com.market.BuyFromHome.enums.CartStatus;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.*;
import com.market.BuyFromHome.repository.CartItemRepository;
import com.market.BuyFromHome.repository.CartRepository;
import com.market.BuyFromHome.repository.ProductSellingMeasurementRepository;
import com.market.BuyFromHome.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSellingMeasurementRepository productSellingMeasurementRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public CartResponseDto getCart(Long userId) {

        return cartRepository.findByUser_UserId(userId)
                .map(this::mapToResponse)
                .orElseGet(() ->
                        CartResponseDto.builder()
                                .cartId(null)
                                .userId(userId)
                                .status(CartStatus.ACTIVE)
                                .items(List.of())
                                .totalAmount(BigDecimal.ZERO)
                                .build()
                );
    }


    @Transactional
    @Override
    public CartResponseDto addItem(
            Long userId,
            CartItemRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new AppException(
                                "User not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        ProductSellingMeasurement sellingMeasurement =
                productSellingMeasurementRepository
                        .findById(requestDto.getSellingMeasurementId())
                        .orElseThrow(() ->
                                new AppException(
                                        "Selling measurement not found.",
                                        HttpStatus.NOT_FOUND
                                )
                        );

        if (!sellingMeasurement.isEnabled()) {
            throw new AppException(
                    "Selling measurement is disabled.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseGet(() -> {

                    Cart newCart = Cart.builder()
                            .user(user)
                            .status(CartStatus.ACTIVE)
                            .items(new ArrayList<>())
                            .build();

                    return cartRepository.save(newCart);
                });

        CartItem existingCartItem =
                cartItemRepository
                        .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                                cart.getCartId(),
                                sellingMeasurement.getSellingMeasurementId()
                        )
                        .orElse(null);

        if (existingCartItem != null) {

            int newQuantity =
                    existingCartItem.getQuantity()
                            + requestDto.getQuantity();

            if (newQuantity > sellingMeasurement.getQuantityInStock()) {
                throw new AppException(
                        "Insufficient stock.",
                        HttpStatus.BAD_REQUEST
                );
            }

            existingCartItem.setQuantity(newQuantity);

            cartItemRepository.save(existingCartItem);

        } else {

            if (requestDto.getQuantity()
                    > sellingMeasurement.getQuantityInStock()) {

                throw new AppException(
                        "Insufficient stock.",
                        HttpStatus.BAD_REQUEST
                );
            }

            CartItem cartItem =
                    CartItem.builder()
                            .cart(cart)
                            .sellingMeasurement(sellingMeasurement)
                            .quantity(requestDto.getQuantity())
                            .priceAtTimeOfAdding(
                                    sellingMeasurement.getSellingPrice()
                            )
                            .build();

            cart.getItems().add(cartItem);

            cartItemRepository.save(cartItem);
        }

        return mapToResponse(cart);
    }

    @Transactional
    @Override
    public CartResponseDto updateItemQuantity(
            Long userId,
            Long cartItemId,
            int quantity) {

        if (quantity < 1) {
            throw new AppException(
                    "Quantity must be at least 1. Use remove to delete an item.",
                    HttpStatus.BAD_REQUEST
            );
        }

        Cart cart =
                cartRepository.findByUser_UserId(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Cart not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        CartItem cartItem =
                cartItemRepository
                        .findByCart_CartIdAndCartItemId(
                                cart.getCartId(),
                                cartItemId
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        "Cart item not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        ProductSellingMeasurement sellingMeasurement =
                cartItem.getSellingMeasurement();

        if (!sellingMeasurement.isEnabled()) {
            throw new AppException(
                    "Selling measurement is disabled.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (quantity > sellingMeasurement.getQuantityInStock()) {
            throw new AppException(
                    "Insufficient stock.",
                    HttpStatus.BAD_REQUEST
            );
        }

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return mapToResponse(cart);
    }


    @Transactional
    @Override
    public CartResponseDto removeItem(
            Long userId,
            Long cartItemId) {

        Cart cart =
                cartRepository.findByUser_UserId(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Cart not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        CartItem cartItem =
                cartItemRepository
                        .findByCart_CartIdAndCartItemId(
                                cart.getCartId(),
                                cartItemId
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        "Cart item not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        cart.getItems().remove(cartItem);

        cartItemRepository.delete(cartItem);

        return mapToResponse(cart);
    }

    @Transactional
    @Override
    public CartResponseDto clearCart(Long userId) {

        Cart cart =
                cartRepository.findByUser_UserId(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Cart not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        List<CartItem> items =
                new ArrayList<>(cart.getItems());

        cartItemRepository.deleteAll(items);

        cart.getItems().clear();

        return mapToResponse(cart);
    }






    private CartResponseDto mapToResponse(Cart cart) {

        List<CartItemResponseDto> items =
                cart.getItems()
                        .stream()
                        .map(cartItem -> {

                            ProductSellingMeasurement measurement =
                                    cartItem.getSellingMeasurement();

                            ProductOption option =
                                    measurement.getProductOption();

                            Product product =
                                    option.getProduct();

                            ProductCategory category =
                                    product.getCategory();

                            BigDecimal subtotal =
                                    cartItem.getPriceAtTimeOfAdding()
                                            .multiply(
                                                    BigDecimal.valueOf(
                                                            cartItem.getQuantity()
                                                    )
                                            );

                            return CartItemResponseDto.builder()
                                    .cartItemId(
                                            cartItem.getCartItemId()
                                    )
                                    .categoryId(
                                            category.getId()
                                    )
                                    .categoryName(
                                            category.getName()
                                    )
                                    .productId(
                                            product.getProductId()
                                    )
                                    .productName(
                                            product.getProductName()
                                    )
                                    .productOptionId(
                                            option.getProductOptionId()
                                    )
                                    .productVariety(
                                            option.getProductVariety()
                                    )
                                    .productSpecification(
                                            option.getProductSpecification()
                                    )
                                    .sellingMeasurementId(
                                            measurement.getSellingMeasurementId()
                                    )
                                    .measurementUnit(
                                            measurement
                                                    .getMeasurementUnit()
                                                    .name()
                                    )
                                    .quantity(
                                            cartItem.getQuantity()
                                    )
                                    .priceAtTimeOfAdding(
                                            cartItem.getPriceAtTimeOfAdding()
                                    )
                                    .subtotal(subtotal)
                                    .build();
                        })
                        .toList();

        BigDecimal totalAmount =
                items.stream()
                        .map(CartItemResponseDto::getSubtotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return CartResponseDto.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUser().getUserId())
                .status(cart.getStatus())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }
}
