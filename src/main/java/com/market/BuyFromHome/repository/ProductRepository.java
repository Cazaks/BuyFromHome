package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductNameIgnoreCase(String productName);


    boolean existsByProductNameIgnoreCaseAndProductIdNot(
            String productName, Long productId);
}
