package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<ProductCategory> findByNameIgnoreCase(String name);
}
