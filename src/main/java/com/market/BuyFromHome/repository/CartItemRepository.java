package com.market.BuyFromHome.repository;
import com.market.BuyFromHome.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCart_CartIdAndSellingMeasurement_SellingMeasurementId(
            Long cartId,
            Long sellingMeasurementId
    );
}