package com.market.BuyFromHome.repository;

import com.market.BuyFromHome.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct_ProductId(Long productId);

    List<Review> findByUser_UserId(Long userId);

    // Ownership-scoped lookup, same pattern as
    // AddressRepository.findByAddressIdAndUser_UserId
    Optional<Review> findByReviewIdAndUser_UserId(Long reviewId, Long userId);

    // Enforces "one review per user per product" in the service layer,
    // backed by the DB-level unique constraint on the entity as a
    // second line of defense against race conditions.
    boolean existsByUser_UserIdAndProduct_ProductId(Long userId, Long productId);
}