package turtleMart.review.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import turtleMart.review.entity.Review;
import turtleMart.review.entity.ReviewDocument;

public interface ReviewElasticSearchRepository extends ElasticsearchRepository<ReviewDocument, Long> {
}
