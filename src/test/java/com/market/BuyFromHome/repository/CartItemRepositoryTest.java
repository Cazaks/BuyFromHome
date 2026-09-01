package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.enums.AuthProvider;
import com.market.BuyFromHome.enums.CartStatus;
import com.market.BuyFromHome.enums.MeasurementUnit;
import com.market.BuyFromHome.enums.Role;
import com.market.BuyFromHome.model.Cart;
import com.market.BuyFromHome.model.CartItem;
import com.market.BuyFromHome.model.Product;
import com.market.BuyFromHome.model.ProductCategory;
import com.market.BuyFromHome.model.ProductOption;
import com.market.BuyFromHome.model.ProductSellingMeasurement;
import com.market.BuyFromHome.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductSellingMeasurementRepository
            productSellingMeasurementRepository;


    @Test
    @DisplayName(
            "Should find cart item by cart id and selling measurement id"
    )
    void shouldFindCartItemByCartIdAndSellingMeasurementId() {

        User user = userRepository.save(buildUser());

        ProductCategory category =
                productCategoryRepository.save(
                        buildProductCategory()
                );

        Product product =
                Product.builder()
                        .productName("Rice")
                        .category(category)
                        .build();

        product =
                productRepository.save(product);

        ProductOption productOption =
                ProductOption.builder()
                        .product(product)
                        .productVariety("Local Rice")
                        .productSpecification("Short Grain")
                        .build();

        productOption =
                productOptionRepository.save(productOption);

        ProductSellingMeasurement measurement =
                ProductSellingMeasurement.builder()
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(
                                new BigDecimal("2500.00")
                        )
                        .quantityInStock(20)
                        .enabled(true)
                        .build();

        measurement =
                productSellingMeasurementRepository
                        .save(measurement);

        Cart cart =
                Cart.builder()
                        .user(user)
                        .status(CartStatus.ACTIVE)
                        .build();

        cart =
                cartRepository.save(cart);

        CartItem cartItem =
                CartItem.builder()
                        .cart(cart)
                        .sellingMeasurement(measurement)
                        .quantity(2)
                        .priceAtTimeOfAdding(
                                measurement.getSellingPrice()
                        )
                        .build();

        cartItem =
                cartItemRepository.save(cartItem);

        Optional<CartItem> result =
                cartItemRepository
                        .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
                                cart.getCartId(),
                                measurement.getSellingMeasurementId()
                        );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getCartItemId())
                .isEqualTo(cartItem.getCartItemId());

        assertThat(result.get().getCart())
                .isEqualTo(cart);

        assertThat(result.get().getSellingMeasurement())
                .isEqualTo(measurement);

        assertThat(result.get().getQuantity())
                .isEqualTo(2);
    }


    @Test
    @DisplayName(
            "Should find cart item by cart id and cart item id"
    )
    void shouldFindCartItemByCartIdAndCartItemId() {

        User user = userRepository.save(buildUser());

        ProductCategory category =
                productCategoryRepository.save(
                        buildProductCategory()
                );

        Product product =
                Product.builder()
                        .productName("Rice")
                        .category(category)
                        .build();

        product =
                productRepository.save(product);

        ProductOption productOption =
                ProductOption.builder()
                        .product(product)
                        .productVariety("Local Rice")
                        .productSpecification("Short Grain")
                        .build();

        productOption =
                productOptionRepository.save(productOption);

        ProductSellingMeasurement measurement =
                ProductSellingMeasurement.builder()
                        .productOption(productOption)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(
                                new BigDecimal("2500.00")
                        )
                        .quantityInStock(20)
                        .enabled(true)
                        .build();

        measurement =
                productSellingMeasurementRepository
                        .save(measurement);

        Cart cart =
                Cart.builder()
                        .user(user)
                        .status(CartStatus.ACTIVE)
                        .build();

        cart =
                cartRepository.save(cart);

        CartItem cartItem =
                CartItem.builder()
                        .cart(cart)
                        .sellingMeasurement(measurement)
                        .quantity(2)
                        .priceAtTimeOfAdding(
                                measurement.getSellingPrice()
                        )
                        .build();

        cartItem =
                cartItemRepository.save(cartItem);

        Optional<CartItem> result =
                cartItemRepository
                        .findByCart_CartIdAndCartItemId(
                                cart.getCartId(),
                                cartItem.getCartItemId()
                        );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getCartItemId())
                .isEqualTo(cartItem.getCartItemId());

        assertThat(result.get().getCart())
                .isEqualTo(cart);
    }


    private User buildUser() {

        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password")
                .phoneNumber("08012345678")
                .role(Role.CUSTOMER)
                .provider(AuthProvider.LOCAL)
                .enabled(true)
                .build();
    }


    private ProductCategory buildProductCategory() {

        return ProductCategory.builder()
                .name("Grain")
                .description("Grain products")
                .enabled(true)
                .build();
    }


}