package turtleMart.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import turtleMart.review.dto.request.CreateReviewRequest;
import turtleMart.review.dto.request.UpdateReviewRequest;
import turtleMart.review.dto.response.ReviewResponse;
import turtleMart.review.service.ReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            //@RequestAttribute("memberId") Long memberId,
            @PathVariable(name = "productId") Long productId,
            @RequestBody @Valid CreateReviewRequest request) {

        ReviewResponse reviewResponse = reviewService.createReview(1L, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponse);
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> readReview(
            @PathVariable(name = "reviewId") Long reviewId) {

        ReviewResponse reviewResponse = reviewService.readReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(reviewResponse);
    }

    @GetMapping("/members/reviews")
    public ResponseEntity<Page<ReviewResponse>> readByMemberId(//@RequestAttribute("memberId") Long memberId,
                                                               @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                               @RequestParam(name = "page", required = false, defaultValue = "1" ) int page
    ) {
        Pageable pageable = PageRequest.of(page - 1 , size);
        Page<ReviewResponse> reviewResponse = reviewService.readByMemberId(1L, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(reviewResponse);
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<ReviewResponse>> readByProductIdAndCondition(@PathVariable(name = "productId") Long productId,
                                                                @RequestParam(name = "keyWord", required = false) String keyWord,
                                                                @RequestParam(name = "rating", required = false) Integer rating,
                                                                @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ){
        Pageable pageable = PageRequest.of(page - 1, size);
        List<ReviewResponse> reviewResponse = reviewService.readByProductIdWithSearch(productId, keyWord, rating, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(reviewResponse);
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            //@RequestAttribute("memberId") Long memberId,
            @PathVariable(name = "reviewId") Long reviewId,
            @RequestBody UpdateReviewRequest request){

        ReviewResponse reviewResponse = reviewService.updateReview(1L, reviewId, request);
        return ResponseEntity.status(HttpStatus.OK).body(reviewResponse);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            //@RequestAttribute("memberId") Long memberId,
            @PathVariable(name = "reviewId") Long reviewId) {

        reviewService.deleteReview(1L, reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
