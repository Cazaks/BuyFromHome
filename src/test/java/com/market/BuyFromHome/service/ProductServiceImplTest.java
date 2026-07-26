package com.market.BuyFromHome.service;

import com.market.BuyFromHome.dto.requestDto.productRequest.ProductRequestDto;
import com.market.BuyFromHome.dto.responseDto.productResponse.ProductResponseDto;
import com.market.BuyFromHome.exception.AppException;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.repository.ProductCategoryRepository;
import com.market.BuyFromHome.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    @DisplayName("Should create product successfully")
    void createProductSuccessfully(){

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Rice");
        requestDto.setProductDescription("Long grain foreign rice");
        requestDto.setProductCategoryId(1L);

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grain")
                .build();

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto responseDto =
                productServiceImpl.createProduct(requestDto);

        assertThat(responseDto.getProductName()).isEqualTo("Rice");
        assertThat(responseDto.getProductDescription())
                .isEqualTo("Long grain foreign rice");
        assertThat(responseDto.getProductCategoryId()).isEqualTo(1L);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product already exists")
    void throwProductAlreadyExists() {

        ProductRequestDto requestDto = new ProductRequestDto();

        requestDto.setProductName("Rice");
        requestDto.setProductDescription("50kg Bag of Rice");
        requestDto.setProductCategoryId(1L);

        when(productRepository.existsByProductNameIgnoreCase("Rice"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                productServiceImpl.createProduct(requestDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product already exists")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product category does not exist")
    void throwProductCategoryNotFound() {

        ProductRequestDto requestDto = new ProductRequestDto();

        requestDto.setProductName("Rice");
        requestDto.setProductDescription("50kg Bag of Rice");
        requestDto.setProductCategoryId(1L);

        when(productRepository.existsByProductNameIgnoreCase("Rice"))
                .thenReturn(false);

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productServiceImpl.createProduct(requestDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product category not found")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should save product with correct details")
    void shouldSaveProductWithCorrectDetails() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Rice");
        requestDto.setProductDescription("50kg Bag of Rice");
        requestDto.setProductCategoryId(1L);

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        when(productRepository.existsByProductNameIgnoreCase("Rice"))
                .thenReturn(false);

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        productServiceImpl.createProduct(requestDto);

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();

        assertThat(savedProduct.getProductName()).isEqualTo("Rice");
        assertThat(savedProduct.getProductDescription())
                .isEqualTo("50kg Bag of Rice");
        assertThat(savedProduct.getCategory()).isEqualTo(category);
        assertThat(savedProduct.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should return created product")
    void shouldReturnCreatedProduct() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Rice");
        requestDto.setProductDescription("50kg Bag of Rice");
        requestDto.setProductCategoryId(1L);

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product savedProduct = Product.builder()
                .productId(1L)
                .productName("Rice")
                .productDescription("50kg Bag of Rice")
                .category(category)
                .enabled(true)
                .build();

        when(productRepository.existsByProductNameIgnoreCase("Rice"))
                .thenReturn(false);

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductResponseDto response =
                productServiceImpl.createProduct(requestDto);

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Rice");
        assertThat(response.getProductDescription())
                .isEqualTo("50kg Bag of Rice");
        assertThat(response.getProductCategoryId()).isEqualTo(1L);
        assertThat(response.getProductCategoryName()).isEqualTo("Grains");
        assertThat(response.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should return product by id")
    void shouldReturnProductById() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .productDescription("50kg Bag of Rice")
                .category(category)
                .enabled(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponseDto response =
                productServiceImpl.getProductById(1L);

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Rice");
        assertThat(response.getProductDescription())
                .isEqualTo("50kg Bag of Rice");
        assertThat(response.getProductCategoryId()).isEqualTo(1L);
        assertThat(response.getProductCategoryName()).isEqualTo("Grains");
        assertThat(response.isEnabled()).isTrue();

        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product is not found")
    void throwProductNotFound() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productServiceImpl.getProductById(1L))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product not found")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return all products")
    void shouldReturnAllProducts() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product product1 = Product.builder()
                .productId(1L)
                .productName("Rice")
                .productDescription("50kg Bag of Rice")
                .category(category)
                .enabled(true)
                .build();

        Product product2 = Product.builder()
                .productId(2L)
                .productName("Beans")
                .productDescription("White Beans")
                .category(category)
                .enabled(true)
                .build();

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<ProductResponseDto> response =
                productServiceImpl.getAllProducts();

        assertThat(response).hasSize(2);

        assertThat(response.get(0).getProductName()).isEqualTo("Rice");
        assertThat(response.get(1).getProductName()).isEqualTo("Beans");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProductsExist() {

        when(productRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<ProductResponseDto> response =
                productServiceImpl.getAllProducts();

        assertThat(response).isEmpty();

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Beans");
        requestDto.setProductDescription("White Beans");
        requestDto.setProductCategoryId(2L);

        ProductCategory oldCategory = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        ProductCategory newCategory = ProductCategory.builder()
                .id(2L)
                .name("Food Stuff")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .productDescription("50kg Bag of Rice")
                .category(oldCategory)
                .enabled(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.existsByProductNameIgnoreCase("Beans"))
                .thenReturn(false);

        when(productCategoryRepository.findById(2L))
                .thenReturn(Optional.of(newCategory));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response =
                productServiceImpl.updateProduct(1L, requestDto);

        assertThat(response.getProductName()).isEqualTo("Beans");
        assertThat(response.getProductDescription())
                .isEqualTo("White Beans");
        assertThat(response.getProductCategoryId()).isEqualTo(2L);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product is not found")
    void throwProductNotFoundWhenUpdating() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Beans");
        requestDto.setProductDescription("White Beans");
        requestDto.setProductCategoryId(2L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productServiceImpl.updateProduct(1L, requestDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product not found")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to an existing product name")
    void throwProductAlreadyExistsWhenUpdating() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Beans");
        requestDto.setProductDescription("White Beans");
        requestDto.setProductCategoryId(2L);

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .productDescription("50kg Bag of Rice")
                .category(category)
                .enabled(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.existsByProductNameIgnoreCase("Beans"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                productServiceImpl.updateProduct(1L, requestDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product already exists")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should allow updating when product name is unchanged")
    void shouldAllowUpdatingWhenProductNameIsUnchanged() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Rice");
        requestDto.setProductCategoryId(1L);

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .category(category)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.existsByProductNameIgnoreCaseAndProductIdNot("Rice", 1L))
                .thenReturn(false);

        when(productCategoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        productServiceImpl.updateProduct(1L, requestDto);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product category is not found")
    void throwProductCategoryNotFoundWhenUpdating() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Beans");
        requestDto.setProductDescription("White Beans");
        requestDto.setProductCategoryId(2L);

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .productDescription("50kg Bag of Rice")
                .category(category)
                .enabled(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.existsByProductNameIgnoreCaseAndProductIdNot("Beans", 1L))
                .thenReturn(false);

        when(productCategoryRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productServiceImpl.updateProduct(1L, requestDto))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product category not found")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should save updated product with correct details")
    void shouldSaveUpdatedProductWithCorrectDetails() {

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setProductName("Beans");
        requestDto.setProductDescription("White Beans");
        requestDto.setProductCategoryId(2L);

        ProductCategory oldCategory = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        ProductCategory newCategory = ProductCategory.builder()
                .id(2L)
                .name("Food Stuff")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .productDescription("50kg Bag of Rice")
                .category(oldCategory)
                .enabled(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.existsByProductNameIgnoreCaseAndProductIdNot("Beans", 1L))
                .thenReturn(false);

        when(productCategoryRepository.findById(2L))
                .thenReturn(Optional.of(newCategory));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        productServiceImpl.updateProduct(1L, requestDto);

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());

        Product updatedProduct = productCaptor.getValue();

        assertThat(updatedProduct.getProductName()).isEqualTo("Beans");
        assertThat(updatedProduct.getProductDescription())
                .isEqualTo("White Beans");
        assertThat(updatedProduct.getCategory()).isEqualTo(newCategory);
    }

    @Test
    @DisplayName("Should disable product successfully")
    void shouldDisableProductSuccessfully() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .category(category)
                .enabled(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response =
                productServiceImpl.disableProduct(1L);

        assertThat(response.isEnabled()).isFalse();

        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should throw exception when product is not found while disabling")
    void throwProductNotFoundWhenDisabling() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productServiceImpl.disableProduct(1L))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product not found")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should enable product successfully")
    void shouldEnableProductSuccessfully() {

        ProductCategory category = ProductCategory.builder()
                .id(1L)
                .name("Grains")
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productName("Rice")
                .category(category)
                .enabled(false)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response =
                productServiceImpl.enableProduct(1L);

        assertThat(response.isEnabled()).isTrue();

        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should throw exception when product is not found while enabling")
    void throwProductNotFoundWhenEnabling() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productServiceImpl.enableProduct(1L))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Product not found")
                .extracting(ex -> ((AppException) ex).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(productRepository, never()).save(any(Product.class));
    }

}