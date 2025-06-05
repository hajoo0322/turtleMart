//package turtleMart.review.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Component;
//import turtleMart.review.entity.Review;
//import turtleMart.review.entity.ReviewDocument;
//import turtleMart.review.repository.ReviewElasticSearchRepository;
//import turtleMart.review.repository.ReviewRepository;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class ReviewDataSeeder implements CommandLineRunner {
//
//    private final OrderItemGenerator orderItemGenerator;
//    private final ReviewRepository reviewRepository;
//    private final ReviewElasticSearchRepository reviewElasticSearchRepository;
//    private final ReviewDummyGenerator generator;
//
//    @Override
//    public void run(String... args) {
////        orderItemGenerator.generatorOrderItem(30000);
////        generator.generate(30000, 100); // 총 100개의 상품에 대해 10,000개의 리뷰 생성
//
//        int page = 0;
//        int size = 1000;
//        Page<Review> reviewPage;
//
//        do {
//            Pageable pageable = PageRequest.of(page, size);
//            reviewPage = reviewRepository.findAll(pageable); // JPA에서 데이터 가져옴
//
//            List<ReviewDocument> esDocs = reviewPage.getContent().stream()
//                    .map(r -> ReviewDocument.of(r.getId(), r.getProduct().getId(),  r.getTitle(), r.getContent(), r.getRating())) // -> Elasticsearch 문서로 변환
//                    .toList();
//
//            reviewElasticSearchRepository.saveAll(esDocs); // Elasticsearch 저장
//
//            page++;
//        } while (reviewPage.hasNext());
//    }
//}