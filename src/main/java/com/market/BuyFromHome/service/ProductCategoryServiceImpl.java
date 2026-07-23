package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productCategoryRequest.ProductCategoryRequestDto;
import com.market.BuyFromHome.dto.responseDto.productCategoryResponse.ProductCategoryResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService{

    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    @Override
    public ProductCategoryResponseDto createProductCategory(
            ProductCategoryRequestDto requestDto) {

        if (productCategoryRepository.existsByNameIgnoreCase(requestDto.getName())) {
            throw new AppException(
                    "Product category already exists.",
                    HttpStatus.CONFLICT
            );
        }

        ProductCategory productCategory = ProductCategory.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .enabled(true)
                .build();

        ProductCategory savedCategory =
                productCategoryRepository.save(productCategory);

        return mapToResponse(savedCategory);
    }

    @Transactional
    @Override
    public List<ProductCategoryResponseDto> getAllProductCategories() {

        return productCategoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Transactional
    @Override
    public ProductCategoryResponseDto updateProductCategory(
            Long id,
            ProductCategoryRequestDto requestDto) {

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(
                                "Product category not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        if (!category.getName().equalsIgnoreCase(requestDto.getName())
                && productCategoryRepository.existsByNameIgnoreCase(requestDto.getName())) {

            throw new AppException(
                    "Product category already exists.",
                    HttpStatus.CONFLICT
            );
        }

        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());

        ProductCategory updatedCategory =
                productCategoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }


    @Transactional
    @Override
    public ProductCategoryResponseDto getProductCategoryById(Long id){

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(
                                "Product category not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        return mapToResponse(category);
    }



    @Transactional
    @Override
    public ProductCategoryResponseDto disableProductCategory(Long id){

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(
                                "Product category not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        category.setEnabled(false);

        ProductCategory disableCategory = productCategoryRepository.save(category);

        return mapToResponse(disableCategory);
    }

    @Transactional
    @Override
    public ProductCategoryResponseDto enableProductCategory(Long id){

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(
                                "Product category not found.",
                                HttpStatus.NOT_FOUND
                        )
                );

        category.setEnabled(true);

        ProductCategory enableCategory = productCategoryRepository.save(category);

        return mapToResponse(enableCategory);
    }

    private ProductCategoryResponseDto mapToResponse(ProductCategory category) {

        return ProductCategoryResponseDto.builder()
                .id(category.getId())
                .categoryName(category.getName())
                .categoryDescription(category.getDescription())
                .enabled(category.isEnabled())
                .build();
    }


}
