package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.responseDto.cartItemResponse.CartItemResponseDto;
import com.market.BuyFromHome.dto.responseDto.cartResponse.CartResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.*;
import com.market.BuyFromHome.repository.CartItemRepository;
import com.market.BuyFromHome.repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public CartResponseDto getCart(Long userId) {

        Cart cart =
                cartRepository.findByUser_UserId(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Cart not found.",
                                        HttpStatus.NOT_FOUND
                                ));

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
                                    .cartItemId(cartItem.getCartItemId())
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
                                            measurement
                                                    .getSellingMeasurementId()
                                    )
                                    .measurementUnit(
                                            measurement
                                                    .getMeasurementUnit()
                                                    .name()
                                    )
                                    .quantity(cartItem.getQuantity())
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
