package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.cartItemRequest.CartItemRequestDto;
import com.market.BuyFromHome.dto.responseDto.cartResponse.CartResponseDto;
import com.market.BuyFromHome.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartServiceController {

    private final CartService cartService;

    // ==========================
    // GET USER CART
    // ==========================
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDto> getCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }

    // ==========================
    // ADD ITEM TO CART
    // ==========================
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponseDto> addItem(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequestDto requestDto) {

        return ResponseEntity.ok(
                cartService.addItem(
                        userId,
                        requestDto
                )
        );
    }

    // ==========================
    // UPDATE ITEM QUANTITY
    // ==========================
    @PatchMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @RequestParam int quantity) {

        return ResponseEntity.ok(
                cartService.updateItemQuantity(
                        userId,
                        cartItemId,
                        quantity
                )
        );
    }

    // ==========================
    // REMOVE ITEM FROM CART
    // ==========================
    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {

        return ResponseEntity.ok(
                cartService.removeItem(
                        userId,
                        cartItemId
                )
        );
    }

    // ==========================
    // CLEAR CART
    // ==========================
    @DeleteMapping("/{userId}/items")
    public ResponseEntity<CartResponseDto> clearCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                cartService.clearCart(userId)
        );
    }
}