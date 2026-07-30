package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.enums.MeasurementUnit;
import com.market.BuyFromHome.model.ProductSellingMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSellingMeasurementRepository
        extends JpaRepository<ProductSellingMeasurement, Long> {

    boolean existsByProductOption_ProductOptionIdAndMeasurementUnit(
            Long productOptionId,
            MeasurementUnit measurementUnit
    );

    List<ProductSellingMeasurement>
    findByProductOption_ProductOptionIdAndEnabledTrue(Long productOptionId);
}