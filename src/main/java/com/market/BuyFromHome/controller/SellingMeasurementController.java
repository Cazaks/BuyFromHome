package com.market.BuyFromHome.controller;

import com.market.BuyFromHome.dto.requestDto.productSellingRequest.ProductSellingMeasurementRequestDto;
import com.market.BuyFromHome.dto.responseDto.productSellingResponse.ProductSellingMeasurementResponseDto;
import com.market.BuyFromHome.service.ProductSellingMeasurementService;
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
@RequestMapping("/api/v1/selling-measurements")
@RequiredArgsConstructor
@Tag(
        name = "Selling Measurements",
        description = "Endpoints for managing product selling measurements"
)
public class SellingMeasurementController {

    private final ProductSellingMeasurementService productSellingMeasurementService;

    // ==========================
    // CREATE SELLING MEASUREMENT
    // ==========================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new selling measurement")
    public ResponseEntity<ProductSellingMeasurementResponseDto> createSellingMeasurement(
            @Valid @RequestBody ProductSellingMeasurementRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productSellingMeasurementService.createSellingMeasurement(requestDto));
    }

    // ==========================
    // GET SELLING MEASUREMENT BY ID
    // ==========================
    @GetMapping("/{sellingMeasurementId}")
    @Operation(summary = "Get a selling measurement by its ID")
    public ResponseEntity<ProductSellingMeasurementResponseDto> getSellingMeasurementById(
            @PathVariable Long sellingMeasurementId) {

        return ResponseEntity.ok(
                productSellingMeasurementService.getSellingMeasurementById(
                        sellingMeasurementId));
    }

    // ==========================
    // GET ALL SELLING MEASUREMENTS
    // ==========================
    @GetMapping
    @Operation(summary = "Retrieve all selling measurements")
    public ResponseEntity<List<ProductSellingMeasurementResponseDto>> getAllSellingMeasurements() {

        return ResponseEntity.ok(
                productSellingMeasurementService.getAllSellingMeasurements());
    }

    // ==========================
    // GET BY PRODUCT OPTION
    // ==========================
    @GetMapping("/product-option/{productOptionId}")
    @Operation(summary = "Retrieve selling measurements for a product option")
    public ResponseEntity<List<ProductSellingMeasurementResponseDto>> getSellingMeasurementsByProductOption(
            @PathVariable Long productOptionId) {

        return ResponseEntity.ok(
                productSellingMeasurementService
                        .getSellingMeasurementsByProductOption(productOptionId));
    }

    // ==========================
    // UPDATE SELLING MEASUREMENT
    // ==========================
    @PutMapping("/{sellingMeasurementId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing selling measurement")
    public ResponseEntity<ProductSellingMeasurementResponseDto> updateSellingMeasurement(
            @PathVariable Long sellingMeasurementId,
            @Valid @RequestBody ProductSellingMeasurementRequestDto requestDto) {

        return ResponseEntity.ok(
                productSellingMeasurementService.updateSellingMeasurement(
                        sellingMeasurementId,
                        requestDto));
    }

    // ==========================
    // DISABLE SELLING MEASUREMENT
    // ==========================
    @PatchMapping("/{sellingMeasurementId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable a selling measurement")
    public ResponseEntity<ProductSellingMeasurementResponseDto> disableSellingMeasurement(
            @PathVariable Long sellingMeasurementId) {

        return ResponseEntity.ok(
                productSellingMeasurementService.disableSellingMeasurement(
                        sellingMeasurementId));
    }

    // ==========================
    // ENABLE SELLING MEASUREMENT
    // ==========================
    @PatchMapping("/{sellingMeasurementId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable a previously disabled selling measurement")
    public ResponseEntity<ProductSellingMeasurementResponseDto> enableSellingMeasurement(
            @PathVariable Long sellingMeasurementId) {

        return ResponseEntity.ok(
                productSellingMeasurementService.enableSellingMeasurement(
                        sellingMeasurementId));
    }
}