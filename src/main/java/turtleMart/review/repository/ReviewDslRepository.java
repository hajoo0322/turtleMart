package turtleMart.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import turtleMart.review.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDslRepository {

    Optional<Review> findByIdWithChoice(Long reviewId);

    Page<Review> findByMemberIdWithPagination(Long memberId, Pageable pageable);

    List<Review> findByProductWithSearch(Long productId, String keyWord, Integer rating, Integer cursor);

    List<Review> findByIdInWithPagination(List<Long> reviewIdList);
}
