package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.config.ApplicationConfig;
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
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ApplicationConfig.class)
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
    private ProductSellingMeasurementRepository productSellingMeasurementRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find cart item by cart id and selling measurement id")
    void shouldFindCartItemByCartIdAndSellingMeasurementId() {
        CartItem saved = createCartItem();
        Long cartId = saved.getCart().getCartId();
        Long measurementId = saved.getSellingMeasurement().getSellingMeasurementId();

        Optional<CartItem> result = cartItemRepository
                .findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(cartId, measurementId);

        assertThat(result).isPresent();
        assertThat(result.get().getCartItemId()).isEqualTo(saved.getCartItemId());
        assertThat(result.get().getCart().getCartId()).isEqualTo(cartId);
        assertThat(result.get().getSellingMeasurement().getSellingMeasurementId()).isEqualTo(measurementId);
        assertThat(result.get().getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find cart item by cart id and cart item id")
    void shouldFindCartItemByCartIdAndCartItemId() {
        CartItem saved = createCartItem();
        Long cartId = saved.getCart().getCartId();

        Optional<CartItem> result = cartItemRepository
                .findByCart_CartIdAndCartItemId(cartId, saved.getCartItemId());

        assertThat(result).isPresent();
        assertThat(result.get().getCartItemId()).isEqualTo(saved.getCartItemId());
        assertThat(result.get().getCart().getCartId()).isEqualTo(cartId);
    }

    @Test
    @DisplayName("Should return empty when the cart id does not match")
    void shouldReturnEmptyWhenCartIdIsWrong() {
        CartItem saved = createCartItem();

        Optional<CartItem> result = cartItemRepository
                .findByCart_CartIdAndCartItemId(999999L, saved.getCartItemId());

        assertThat(result).isEmpty();
    }

    private CartItem createCartItem() {
        User user = userRepository.save(buildUser());

        ProductCategory category = productCategoryRepository.save(buildProductCategory());

        Product product = productRepository.save(
                Product.builder().productName("Rice").category(category).build());

        ProductOption option = productOptionRepository.save(
                ProductOption.builder().product(product)
                        .productVariety("Local Rice").productSpecification("Short Grain").build());

        ProductSellingMeasurement measurement = productSellingMeasurementRepository.save(
                ProductSellingMeasurement.builder().productOption(option)
                        .measurementUnit(MeasurementUnit.DERICA)
                        .sellingPrice(new BigDecimal("2500.00"))
                        .quantityInStock(20).enabled(true).build());

        Cart cart = cartRepository.save(
                Cart.builder().user(user).status(CartStatus.ACTIVE).build());

        CartItem cartItem = cartItemRepository.save(
                CartItem.builder().cart(cart).sellingMeasurement(measurement)
                        .quantity(2).priceAtTimeOfAdding(measurement.getSellingPrice()).build());

        entityManager.flush();
        entityManager.clear();
        return cartItem;
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