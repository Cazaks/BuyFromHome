    package com.market.BuyFromHome.model;

    import jakarta.persistence.*;
    import lombok.*;

    import java.math.BigDecimal;

    @Entity
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "order_items")
    public class OrderItem extends BasicEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long orderItemId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id", nullable = false)
        private Order order;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "selling_measurement_id", nullable = false)
        private ProductSellingMeasurement sellingMeasurement;

        @Column(nullable = false)
        private Integer quantity;

        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal unitPrice;

        @Column(nullable = false, precision = 12, scale = 2)
        private BigDecimal subtotal;
    }