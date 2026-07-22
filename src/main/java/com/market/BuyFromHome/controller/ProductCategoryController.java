package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.productCategoryRequest.ProductCategoryRequestDto;
import com.market.BuyFromHome.dto.responseDto.productCategoryResponse.ProductCategoryResponseDto;
import com.market.BuyFromHome.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ProductCategoryResponseDto> createCategory(
            @Valid @RequestBody ProductCategoryRequestDto requestDto){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productCategoryService.createProductCategory(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDto> getCategoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productCategoryService.getProductCategoryById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductCategoryResponseDto>> getAllCategories() {

        return ResponseEntity.ok(
                productCategoryService.getAllProductCategories());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ProductCategoryRequestDto requestDto) {

        return ResponseEntity.ok(
                productCategoryService.updateProductCategory(id, requestDto));
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCategoryResponseDto> disableCategory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productCategoryService.disableProductCategory(id));
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCategoryResponseDto> enableCategory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productCategoryService.enableProductCategory(id));
    }
}
