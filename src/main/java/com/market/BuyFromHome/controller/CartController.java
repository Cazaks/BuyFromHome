package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.cartItemRequest.CartItemRequestDto;
import com.market.BuyFromHome.dto.responseDto.cartResponse.CartResponseDto;
import com.market.BuyFromHome.security.CurrentUserProvider;
import com.market.BuyFromHome.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CurrentUserProvider currentUserProvider;

    // ==========================
    // GET CURRENT USER'S CART
    // ==========================
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart() {
        return ResponseEntity.ok(
                cartService.getCart(currentUserProvider.getCurrentUserId())
        );
    }

    // ==========================
    // ADD ITEM TO CART
    // ==========================
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(
            @Valid @RequestBody CartItemRequestDto requestDto) {
        return ResponseEntity.ok(
                cartService.addItem(
                        currentUserProvider.getCurrentUserId(),
                        requestDto
                )
        );
    }

    // ==========================
    // UPDATE ITEM QUANTITY
    // ==========================
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(
                cartService.updateItemQuantity(
                        currentUserProvider.getCurrentUserId(),
                        cartItemId,
                        quantity
                )
        );
    }

    // ==========================
    // REMOVE ITEM FROM CART
    // ==========================
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeItem(
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(
                cartService.removeItem(
                        currentUserProvider.getCurrentUserId(),
                        cartItemId
                )
        );
    }

    // ==========================
    // CLEAR CART
    // ==========================
    @DeleteMapping("/items")
    public ResponseEntity<CartResponseDto> clearCart() {
        return ResponseEntity.ok(
                cartService.clearCart(currentUserProvider.getCurrentUserId())
        );
    }
}