package com.market.BuyFromHome.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews", uniqueConstraints = {
        // enforces "one review per user per product" at the DB level too,
        // not just in the service layer
        @UniqueConstraint(name = "uq_review_user_product", columnNames = {"product_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The specific DELIVERED order this review is tied to - proof of
    // verified purchase. Kept even after the review exists so you can
    // always trace which purchase justified it.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer rating; // 1-5, enforced by validation on the request DTO

    @Column(length = 2000)
    private String comment;

    // sellerReply intentionally omitted for now - SellerReply.java and
    // SellerReplyRequestDto.java are already built; wire this back in
    // as a follow-up by adding: @Embedded private SellerReply sellerReply;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;
}