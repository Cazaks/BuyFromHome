package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.productRequest.ProductRequestDto;
import com.market.BuyFromHome.dto.responseDto.productResponse.ProductResponseDto;
import com.market.BuyFromHome.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(
        name = "Products",
        description = "Endpoints for managing products"
)
public class ProductController {

    private final ProductService productService;

    // ==========================
    // CREATE PRODUCT
    // ==========================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(requestDto));
    }

    // ==========================
    // GET PRODUCT BY ID
    // ==========================
    @GetMapping("/{id}")
    @Operation(summary = "Get a product by its ID")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ==========================
    // GET ALL PRODUCTS
    // ==========================
    @GetMapping
    @Operation(summary = "Retrieve all products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ==========================
    // UPDATE PRODUCT
    // ==========================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto requestDto) {

        return ResponseEntity.ok(
                productService.updateProduct(id, requestDto));
    }

    // ==========================
    // DISABLE PRODUCT
    // ==========================
    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable a product")
    public ResponseEntity<ProductResponseDto> disableProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.disableProduct(id));
    }

    // ==========================
    // ENABLE PRODUCT
    // ==========================
    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable a previously disabled product")
    public ResponseEntity<ProductResponseDto> enableProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.enableProduct(id));
    }
}