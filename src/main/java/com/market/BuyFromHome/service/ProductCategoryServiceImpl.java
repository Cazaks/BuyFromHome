package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productCategoryRequest.ProductCategoryRequestDto;
import com.market.BuyFromHome.dto.responseDto.productCategoryResponse.ProductCategoryResponseDto;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
            throw new RuntimeException(
                    "Product category already exists: " + requestDto.getName());
        }

        ProductCategory productCategory = ProductCategory.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .enabled(true)
                .build();

        ProductCategory savedCategory =
                productCategoryRepository.save(productCategory);

        return ProductCategoryResponseDto.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .enabled(savedCategory.isEnabled())
                .build();
    }

    @Transactional
    @Override
    public List<ProductCategoryResponseDto> getAllProductCategories() {

        return productCategoryRepository.findAll()
                .stream()
                .map(category -> ProductCategoryResponseDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .enabled(category.isEnabled())
                        .build())
                .toList();

    }


    @Transactional
    @Override
    public ProductCategoryResponseDto updateProductCategory(
            Long id,
            ProductCategoryRequestDto requestDto) {

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product category not found"));


        if (!category.getName().equalsIgnoreCase(requestDto.getName())
                && productCategoryRepository.existsByNameIgnoreCase(requestDto.getName())) {

            throw new RuntimeException(
                    "Product category already exists: " + requestDto.getName());
        }


        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());

        ProductCategory updatedCategory =
                productCategoryRepository.save(category);

        return ProductCategoryResponseDto.builder()
                .id(updatedCategory.getId())
                .name(updatedCategory.getName())
                .description(updatedCategory.getDescription())
                .enabled(updatedCategory.isEnabled())
                .build();
    }


    @Transactional
    @Override
    public ProductCategoryResponseDto getProductCategoryById(Long id){

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product category not found")
                );

        return ProductCategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .enabled(category.isEnabled())
                .build();
    }



    @Transactional
    @Override
    public ProductCategoryResponseDto disableProductCategory(Long id){

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product category not found"));

        category.setEnabled(false);

        ProductCategory disableCategory = productCategoryRepository.save(category);

        return ProductCategoryResponseDto.builder()
                .id(disableCategory.getId())
                .name(disableCategory.getName())
                .description(disableCategory.getDescription())
                .enabled(disableCategory.isEnabled())
                .build();
    }



}
