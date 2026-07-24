package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productRequest.ProductRequestDto;
import com.market.BuyFromHome.dto.responseDto.productResponse.ProductResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;
import com.market.BuyFromHome.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {

        if (productRepository.existsByProductNameIgnoreCase(requestDto.getProductName())) {
            throw new AppException(
                    "Product already exists.",
                    HttpStatus.CONFLICT
            );
        }

        ProductCategory category = productCategoryRepository.findById(
                        requestDto.getProductCategoryId())
                .orElseThrow(() -> new AppException(
                        "Product category not found.",
                        HttpStatus.NOT_FOUND
                ));

        Product product = Product.builder()
                .productName(requestDto.getProductName())
                .productDescription(requestDto.getProductDescription())
                .category(category)
                .enabled(true)
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Transactional
    @Override
    public ProductResponseDto getProductById(Long productId){

       Product product = productRepository.findById(productId)
               .orElseThrow(() ->
                       new AppException(
                               "Product not found",
                               HttpStatus.NOT_FOUND
                       )
               );

       return mapToResponse(product);
    }




    private ProductResponseDto mapToResponse(Product product) {

        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productDescription(product.getProductDescription())
                .productCategoryId(product.getCategory().getId())
                .productCategoryName(product.getCategory().getName())
                .enabled(product.isEnabled())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }



}
