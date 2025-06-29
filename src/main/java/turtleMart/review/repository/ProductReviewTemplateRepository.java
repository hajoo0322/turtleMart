package turtleMart.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turtleMart.review.entity.ProductReviewTemplate;

import java.util.List;

public interface ProductReviewTemplateRepository extends JpaRepository<ProductReviewTemplate, Long> {

    @Query("""
                SELECT p
                FROM ProductReviewTemplate p
                JOIN FETCH p.reviewTemplate
                WHERE p.product.id = :productId
            """)
    List<ProductReviewTemplate> findByProductId(@Param("productId") Long productId);
}
