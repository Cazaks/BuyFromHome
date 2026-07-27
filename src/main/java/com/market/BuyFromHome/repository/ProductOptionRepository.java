package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.model.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    boolean existsByProduct_ProductIdAndProductVarietyIgnoreCaseAndProductSpecificationIgnoreCase(
            Long productId,
            String productVariety,
            String productSpecification
    );


    List<ProductOption> findByProduct_ProductIdAndEnabledTrue(Long productId);
}