package turtleMart.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import turtleMart.global.common.CursorPageResponse;
import turtleMart.review.dto.request.CancelReviewReportRequest;
import turtleMart.review.dto.request.CreateReviewReportRequest;
import turtleMart.review.dto.request.UpdateReviewReportStatusRequest;
import turtleMart.review.dto.response.ReviewReportResponse;
import turtleMart.review.service.ReviewReportService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewReportController {

    private final ReviewReportService reviewReportService;

    @PostMapping("/reviews/{reviewId}/review-reports")
    public ResponseEntity<ReviewReportResponse> createReviewReport(
            //@RequestAttribute("memberId") Long memberId,
            @PathVariable(name = "reviewId") Long reviewId,
            @RequestBody @Valid CreateReviewReportRequest request
    ) {
        ReviewReportResponse reviewReportResponse = reviewReportService.createReviewReport(1L, reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewReportResponse);
    }

    @GetMapping("/review-reports/{reviewReportId}")
    public ResponseEntity<ReviewReportResponse> readById(@PathVariable(name = "reviewReportId") Long reviewReportId) {
        ReviewReportResponse reviewReportResponse = reviewReportService.readById(reviewReportId);
        return ResponseEntity.status(HttpStatus.OK).body(reviewReportResponse);
    }

    @GetMapping("/review-reports")
    public ResponseEntity<CursorPageResponse<ReviewReportResponse>> readAll(@RequestParam(name = "reviewReportStatus", required = false) String reviewReportStatus,
                                                              @RequestParam(name = "reasonCode", required = false) String reasonCode,
                                                              @RequestParam(name = "cursor", required = false)Long cursor){

        CursorPageResponse<ReviewReportResponse> reportResponseList = reviewReportService.readByCondition(reviewReportStatus, reasonCode, cursor);
        return ResponseEntity.status(HttpStatus.OK).body(reportResponseList);
    }

    @PatchMapping("/review-reports/{reviewReportId}")
    public ResponseEntity<ReviewReportResponse> updateReviewReport(@PathVariable(name = "reviewReportId") Long reviewReportId,
                                                                   @RequestBody UpdateReviewReportStatusRequest request) {
        ReviewReportResponse reviewReportResponse = reviewReportService.updateReviewReport(reviewReportId, request);
        return ResponseEntity.status(HttpStatus.OK).body(reviewReportResponse);
    }

    @DeleteMapping("/review-reports/{reviewReportId}")
    public ResponseEntity<Void> cancelReviewReport(@PathVariable(name = "reviewReportId") Long reviewReportId,
                                                   @RequestBody CancelReviewReportRequest request) {
        reviewReportService.cancelReviewReport(reviewReportId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
