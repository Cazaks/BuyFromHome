package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productOptionRequest.ProductOptionRequestDto;
import com.market.BuyFromHome.dto.responseDto.productOptionResponse.ProductOptionResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.ProductOption;
import com.market.BuyFromHome.repository.ProductOptionRepository;
import com.market.BuyFromHome.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    @Transactional
    @Override
    public ProductOptionResponseDto createProductOption(ProductOptionRequestDto requestDto) {

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() ->
                        new AppException(
                                "Product not found.",
                                HttpStatus.NOT_FOUND
                        ));

        String specification =
                normalizeSpecification(requestDto.getProductSpecification());

        validateProductOptionDoesNotExist(
                product.getProductId(),
                requestDto.getProductVariety(),
                specification
        );

        ProductOption productOption = ProductOption.builder()
                .product(product)
                .productVariety(requestDto.getProductVariety())
                .productSpecification(specification)
                .build();

        ProductOption savedOption =
                productOptionRepository.save(productOption);

        return mapToResponse(savedOption);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductOptionResponseDto getProductOptionById(Long productOptionId) {

        ProductOption productOption =
                productOptionRepository.findById(productOptionId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Product option not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        return mapToResponse(productOption);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductOptionResponseDto> getAllProductOptions(){

        return productOptionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductOptionResponseDto> getProductOptionsByProduct(Long productId) {

        productRepository.findById(productId)
                .orElseThrow(() ->
                        new AppException(
                                "Product not found.",
                                HttpStatus.NOT_FOUND
                        ));

        return productOptionRepository
                .findByProduct_ProductIdAndEnabledTrue(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @Override
    public ProductOptionResponseDto updateProductOption(
            Long productOptionId,
            ProductOptionRequestDto requestDto) {

        ProductOption productOption =
                productOptionRepository.findById(productOptionId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Product option not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        String specification =
                normalizeSpecification(requestDto.getProductSpecification());

        validateProductOptionDoesNotExistForUpdate(
                productOption.getProduct().getProductId(),
                requestDto.getProductVariety(),
                specification,
                productOption.getProductOptionId()
        );

        productOption.setProductVariety(requestDto.getProductVariety());
        productOption.setProductSpecification(specification);

        ProductOption updatedOption =
                productOptionRepository.save(productOption);

        return mapToResponse(updatedOption);
    }

    @Transactional
    @Override
    public void disableProductOption(Long productOptionId) {

        ProductOption productOption =
                productOptionRepository.findById(productOptionId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Product option not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        productOption.setEnabled(false);

        productOptionRepository.save(productOption);
    }


    @Transactional
    @Override
    public void enableProductOption(Long productOptionId) {

        ProductOption productOption =
                productOptionRepository.findById(productOptionId)
                        .orElseThrow(() ->
                                new AppException(
                                        "Product option not found.",
                                        HttpStatus.NOT_FOUND
                                ));

        productOption.setEnabled(true);

        productOptionRepository.save(productOption);
    }


    private String normalizeSpecification(String specification) {

        if (specification == null || specification.isBlank()) {
            return "";
        }

        return specification.trim();
    }

    private void validateProductOptionDoesNotExist(
            Long productId,
            String productVariety,
            String productSpecification) {

        boolean exists =
                productOptionRepository
                        .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
                                productId,
                                productVariety,
                                productSpecification
                        );

        if (exists) {
            throw new AppException(
                    "Product option already exists.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateProductOptionDoesNotExistForUpdate(
            Long productId,
            String productVariety,
            String productSpecification,
            Long productOptionId) {

        boolean exists =
                productOptionRepository
                        .existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCaseAndProductOptionIdNot(
                                productId,
                                productVariety,
                                productSpecification,
                                productOptionId
                        );

        if (exists) {
            throw new AppException(
                    "Product option already exists.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private ProductOptionResponseDto mapToResponse(ProductOption productOption) {

        return ProductOptionResponseDto.builder()
                .productOptionId(productOption.getProductOptionId())
                .productId(productOption.getProduct().getProductId())
                .productName(productOption.getProduct().getProductName())
                .productVariety(productOption.getProductVariety())
                .productSpecification(productOption.getProductSpecification())
                .enabled(productOption.isEnabled())
                .createdAt(productOption.getCreatedAt())
                .updatedAt(productOption.getUpdatedAt())
                .build();
    }
}