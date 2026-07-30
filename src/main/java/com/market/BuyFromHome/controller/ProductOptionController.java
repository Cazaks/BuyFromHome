package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.productOptionRequest.ProductOptionRequestDto;
import com.market.BuyFromHome.dto.responseDto.productOptionResponse.ProductOptionResponseDto;
import com.market.BuyFromHome.service.ProductOptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-options")
@RequiredArgsConstructor
@Tag(
        name = "Product Options",
        description = "Endpoints for managing product options"
)
public class ProductOptionController {

    private final ProductOptionService productOptionService;

    // ==========================
    // CREATE PRODUCT OPTION
    // ==========================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new product option")
    public ResponseEntity<ProductOptionResponseDto> createProductOption(
            @Valid @RequestBody ProductOptionRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productOptionService.createProductOption(requestDto));
    }

    // ==========================
    // GET PRODUCT OPTION BY ID
    // ==========================
    @GetMapping("/{productOptionId}")
    @Operation(summary = "Get a product option by its ID")
    public ResponseEntity<ProductOptionResponseDto> getProductOptionById(
            @PathVariable Long productOptionId) {

        return ResponseEntity.ok(
                productOptionService.getProductOptionById(productOptionId));
    }

    // ==========================
    // GET ALL PRODUCT OPTIONS
    // ==========================
    @GetMapping
    @Operation(summary = "Retrieve all product options")
    public ResponseEntity<List<ProductOptionResponseDto>> getAllProductOptions() {

        return ResponseEntity.ok(
                productOptionService.getAllProductOptions());
    }

    // ==========================
    // GET PRODUCT OPTIONS BY PRODUCT
    // ==========================
    @GetMapping("/product/{productId}")
    @Operation(summary = "Retrieve all product options belonging to a product")
    public ResponseEntity<List<ProductOptionResponseDto>> getProductOptionsByProduct(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                productOptionService.getProductOptionsByProduct(productId));
    }

    // ==========================
    // UPDATE PRODUCT OPTION
    // ==========================
    @PutMapping("/{productOptionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing product option")
    public ResponseEntity<ProductOptionResponseDto> updateProductOption(
            @PathVariable Long productOptionId,
            @Valid @RequestBody ProductOptionRequestDto requestDto) {

        return ResponseEntity.ok(
                productOptionService.updateProductOption(productOptionId, requestDto));
    }

    // ==========================
    // DISABLE PRODUCT OPTION
    // ==========================
    @PatchMapping("/{productOptionId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable a product option")
    public ResponseEntity<ProductOptionResponseDto> disableProductOption(
            @PathVariable Long productOptionId) {

        return ResponseEntity.ok(
                productOptionService.disableProductOption(productOptionId)
        );
    }

    // ==========================
    // ENABLE PRODUCT OPTION
    // ==========================
    @PatchMapping("/{productOptionId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable a previously disabled product option")
    public ResponseEntity enableProductOption(
            @PathVariable Long productOptionId) {

        return ResponseEntity.ok(
                productOptionService.enableProductOption(productOptionId));
    }
}