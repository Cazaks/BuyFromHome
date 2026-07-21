package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productCategoryRequest.ProductCategoryRequestDto;
import com.market.BuyFromHome.dto.responseDto.productCategoryResponse.ProductCategoryResponseDto;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceImplTest {

    @Mock
    ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    ProductCategoryServiceImpl productCategoryServiceImpl;

    @Test
    @DisplayName("Should create product category successfully")
    void createProductCategorySuccessfully() {

        ProductCategoryRequestDto requestDto = new ProductCategoryRequestDto();
        requestDto.setName("Grains");
        requestDto.setDescription("Rice, Beans, Garri");

        when(productCategoryRepository.existsByNameIgnoreCase("Grains"))
                .thenReturn(false);

        when(productCategoryRepository.save(any(ProductCategory.class)))
                .thenAnswer(i -> i.getArgument(0));

        ProductCategoryResponseDto responseDto =
                productCategoryServiceImpl.createProductCategory(requestDto);

        assertThat(responseDto.getName()).isEqualTo("Grains");
        assertThat(responseDto.getDescription())
                .isEqualTo("Rice, Beans, Garri");

        verify(productCategoryRepository)
                .save(any(ProductCategory.class));
    }


    @Test
    @DisplayName("Should throw exception when product category already exists")
    void throwProductCategoryAlreadyExists() {

        ProductCategoryRequestDto requestDto =
                new ProductCategoryRequestDto();

        requestDto.setName("Grains");
        requestDto.setDescription("Rice, Beans, Garri");

        when(productCategoryRepository.existsByNameIgnoreCase("Grains"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                productCategoryServiceImpl.createProductCategory(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product category already exists");
    }

    @Test
    @DisplayName("Should save product category with correct details")
    void shouldSaveProductCategoryWithCorrectDetails() {

        ProductCategoryRequestDto requestDto = new ProductCategoryRequestDto();
        requestDto.setName("Grains");
        requestDto.setDescription("Rice, Beans, Garri");

        when(productCategoryRepository.existsByNameIgnoreCase("Grains"))
                .thenReturn(false);

        when(productCategoryRepository.save(any(ProductCategory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        productCategoryServiceImpl.createProductCategory(requestDto);

        ArgumentCaptor<ProductCategory> categoryCaptor =
                ArgumentCaptor.forClass(ProductCategory.class);

        verify(productCategoryRepository).save(categoryCaptor.capture());

        ProductCategory savedCategory = categoryCaptor.getValue();

        assertThat(savedCategory.getName()).isEqualTo("Grains");
        assertThat(savedCategory.getDescription())
                .isEqualTo("Rice, Beans, Garri");
        assertThat(savedCategory.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should return created product category")
    void shouldReturnCreatedProductCategory() {

        ProductCategoryRequestDto requestDto = new ProductCategoryRequestDto();
        requestDto.setName("Grains");
        requestDto.setDescription("Rice, Beans, Garri");

        ProductCategory savedCategory = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .description("Rice, Beans, Garri")
                .enabled(true)
                .build();

        when(productCategoryRepository.existsByNameIgnoreCase("Grains"))
                .thenReturn(false);

        when(productCategoryRepository.save(any(ProductCategory.class)))
                .thenReturn(savedCategory);

        ProductCategoryResponseDto response =
                productCategoryServiceImpl.createProductCategory(requestDto);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Grains");
        assertThat(response.getDescription()).isEqualTo("Rice, Beans, Garri");
        assertThat(response.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should get product category by id")
    void shouldGetProductCategoryById() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .description("Rice, Beans, Garri")
                .enabled(true)
                .build();

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        ProductCategoryResponseDto response =
                productCategoryServiceImpl.getProductCategoryById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Grains");
        assertThat(response.getDescription())
                .isEqualTo("Rice, Beans, Garri");
        assertThat(response.isEnabled()).isTrue();

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product category is not found")
    void shouldThrowExceptionWhenProductCategoryIsNotFound() {

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productCategoryServiceImpl.getProductCategoryById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product category not found");

        verify(productCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get all product categories")
    void shouldGetAllProductCategories() {

        ProductCategory grains = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .description("Rice, Beans, Garri")
                .enabled(true)
                .build();

        ProductCategory meat = ProductCategory.builder()
                .id(2L)
                .name("Meat")
                .description("Beef, Goat, Chicken")
                .enabled(true)
                .build();

        when(productCategoryRepository.findAll())
                .thenReturn(List.of(grains, meat));

        List<ProductCategoryResponseDto> response =
                productCategoryServiceImpl.getAllProductCategories();

        assertThat(response).hasSize(2);

        assertThat(response.get(0).getName()).isEqualTo("Grains");
        assertThat(response.get(1).getName()).isEqualTo("Meat");

        verify(productCategoryRepository).findAll();
    }

    @Test
    @DisplayName("Should update product category successfully")
    void shouldUpdateProductCategorySuccessfully() {

        ProductCategoryRequestDto requestDto = new ProductCategoryRequestDto();
        requestDto.setName("Cereals");
        requestDto.setDescription("Rice, Beans, Maize");

        ProductCategory existingCategory = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .description("Rice, Beans, Garri")
                .enabled(true)
                .build();

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(existingCategory));

        when(productCategoryRepository.save(any(ProductCategory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductCategoryResponseDto response =
                productCategoryServiceImpl.updateProductCategory(1L, requestDto);

        assertThat(response.getName()).isEqualTo("Cereals");
        assertThat(response.getDescription())
                .isEqualTo("Rice, Beans, Maize");

        verify(productCategoryRepository).save(existingCategory);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing product category")
    void shouldThrowExceptionWhenUpdatingNonExistingProductCategory() {

        ProductCategoryRequestDto requestDto = new ProductCategoryRequestDto();
        requestDto.setName("Grains");
        requestDto.setDescription("Rice, Beans, Garri");

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productCategoryServiceImpl.updateProductCategory(1L, requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product category not found");

        verify(productCategoryRepository).findById(1L);
        verify(productCategoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to an existing category name")
    void shouldThrowExceptionWhenUpdatingToAnExistingCategoryName() {

        ProductCategoryRequestDto requestDto = new ProductCategoryRequestDto();
        requestDto.setName("Meat");
        requestDto.setDescription("Goat, Beef, Chicken");

        ProductCategory existingCategory = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .description("Rice, Beans, Garri")
                .enabled(true)
                .build();

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(existingCategory));

        when(productCategoryRepository.existsByNameIgnoreCase("Meat"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                productCategoryServiceImpl.updateProductCategory(1L, requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product category already exists");

        verify(productCategoryRepository, never())
                .save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should disable product category successfully")
    void shouldDisableProductCategorySuccessfully() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .description("Rice, Beans, Garri")
                .enabled(true)
                .build();

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productCategoryRepository.save(any(ProductCategory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductCategoryResponseDto response =
                productCategoryServiceImpl.disableProductCategory(1L);

        assertThat(response.isEnabled()).isFalse();

        verify(productCategoryRepository).save(category);
    }

    @Test
    @DisplayName("Should throw exception when disabling non-existing product category")
    void shouldThrowExceptionWhenDisablingNonExistingProductCategory() {

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productCategoryServiceImpl.disableProductCategory(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product category not found");

        verify(productCategoryRepository, never())
                .save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should enable product category successfully")
    void shouldEnableProductCategorySuccessfully() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .description("Rice, Beans, Garri")
                .enabled(false)
                .build();

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productCategoryRepository.save(any(ProductCategory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductCategoryResponseDto response =
                productCategoryServiceImpl.enableProductCategory(1L);

        assertThat(response.isEnabled()).isTrue();

        verify(productCategoryRepository).save(category);
    }
}




