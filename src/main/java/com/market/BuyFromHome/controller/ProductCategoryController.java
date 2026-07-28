package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.productCategoryRequest.ProductCategoryRequestDto;
import com.market.BuyFromHome.dto.responseDto.productCategoryResponse.ProductCategoryResponseDto;
import com.market.BuyFromHome.service.ProductCategoryService;
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
@RequestMapping("/api/v1/product-categories")
@RequiredArgsConstructor
@Tag(
        name = "Product Categories",
        description = "Endpoints for managing product categories"
)
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    // ==========================
    // CREATE PRODUCT CATEGORY
    // ==========================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new product category")
    public ResponseEntity<ProductCategoryResponseDto> createCategory(
            @Valid @RequestBody ProductCategoryRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productCategoryService.createProductCategory(requestDto));
    }

    // ==========================
    // GET PRODUCT CATEGORY BY ID
    // ==========================
    @GetMapping("/{id}")
    @Operation(summary = "Get a product category by its ID")
    public ResponseEntity<ProductCategoryResponseDto> getCategoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productCategoryService.getProductCategoryById(id));
    }

    // ==========================
    // GET ALL PRODUCT CATEGORIES
    // ==========================
    @GetMapping
    @Operation(summary = "Retrieve all product categories")
    public ResponseEntity<List<ProductCategoryResponseDto>> getAllCategories() {

        return ResponseEntity.ok(
                productCategoryService.getAllProductCategories());
    }

    // ==========================
    // UPDATE PRODUCT CATEGORY
    // ==========================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing product category")
    public ResponseEntity<ProductCategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ProductCategoryRequestDto requestDto) {

        return ResponseEntity.ok(
                productCategoryService.updateProductCategory(id, requestDto));
    }

    // ==========================
    // DISABLE PRODUCT CATEGORY
    // ==========================
    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable a product category")
    public ResponseEntity<ProductCategoryResponseDto> disableCategory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productCategoryService.disableProductCategory(id));
    }

    // ==========================
    // ENABLE PRODUCT CATEGORY
    // ==========================
    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable a previously disabled product category")
    public ResponseEntity<ProductCategoryResponseDto> enableCategory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productCategoryService.enableProductCategory(id));
    }
}