package turtleMart.review.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.global.exception.BadRequestException;
import turtleMart.global.exception.ErrorCode;
import turtleMart.global.exception.NotFoundException;
import turtleMart.global.exception.RoleMismatchException;
import turtleMart.global.utill.JsonHelper;
import turtleMart.member.entity.Member;
import turtleMart.member.repository.MemberRepository;
import turtleMart.order.entity.OrderItem;
import turtleMart.order.entity.OrderItemStatus;
import turtleMart.order.repository.OrderItemRepository;
import turtleMart.product.entity.Product;
import turtleMart.product.repository.ProductRepository;
import turtleMart.review.dto.request.CreateReviewRequest;
import turtleMart.review.dto.request.CreateTemplateChoiceRequest;
import turtleMart.review.dto.request.UpdateReviewRequest;
import turtleMart.review.dto.request.UpdateTemplateChoiceRequest;
import turtleMart.review.dto.response.ReviewResponse;
import turtleMart.review.dto.response.TemplateChoiceResponse;
import turtleMart.review.entity.*;
import turtleMart.review.repository.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final ProductReviewTemplateDslRepositoryImpl productReviewTemplateDslRepositoryImpl;
    private final ReviewDslRepositoryImpl reviewDslRepositoryImpl;
    private final ReviewElasticSearchRepository reviewElsaRepository;
    private final ReviewElasticSearchQueryClient elasticSearchQueryClient;

    @Transactional
    public ReviewResponse createReview(Long memberId, Long productId, CreateReviewRequest request) {

        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (!orderItemRepository.existsById(request.orderItemId())) {
            throw new NotFoundException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        Product product = productRepository.getReferenceById(productId);
        Member member = memberRepository.getReferenceById(memberId);
        OrderItem orderItem = orderItemRepository.getReferenceById(request.orderItemId());

        if (orderItem.getOrderItemStatus() != OrderItemStatus.CONFIRMED) {
            throw new BadRequestException(ErrorCode.REVIEW_NOT_ALLOWED_BEFORE_CONFIRMATION);
        }

        if (reviewRepository.existsByOrderItemIdAndIsDeletedFalse(orderItem.getId())) {
            throw new BadRequestException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        String dbImageList = JsonHelper.toJson(request.imageUrlList());
        Review review = Review.of(member, product, orderItem, request.title(), request.content(), request.rating(), dbImageList);

        if(request.templateChoiceList() != null){
            List<ProductReviewTemplate> productReviewTemplateList = productReviewTemplateDslRepositoryImpl.findByIdInWithReviewTemplate(
                    request.templateChoiceList().stream().map(CreateTemplateChoiceRequest::productReviewTemplateId).toList());

            request.templateChoiceList().forEach(c -> {
                Long targetId = c.productReviewTemplateId();
                ProductReviewTemplate matchedTemplate = productReviewTemplateList.stream()
                        .filter(p -> p.getId().equals(targetId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_TEMPLATE_NOT_FOUND));

                TemplateChoice templateChoice = TemplateChoice.of(matchedTemplate, TemplateChoiceGrade.of(c.answer()));
                templateChoice.setReview(review);
            });
        }

        reviewRepository.save(review);
        elasticSearchQueryClient.createReviewDocument(review);

        List<TemplateChoiceResponse> choiceResponseList = review.getTemplateChoiceList() != null ? new ArrayList<>() : TemplateChoice.changeResponseByReview(review);
        List<String> imageUrlList = review.getImageUrl().isEmpty() ? new ArrayList<>() : JsonHelper.fromJsonToList(review.getImageUrl(), new TypeReference<>() {});
        return ReviewResponse.of(review, imageUrlList , choiceResponseList);
    }

    public ReviewResponse readReview(Long reviewId) {

        Review review = findByIdElseThrow(reviewId);

        List<TemplateChoiceResponse> choiceResponseList = review.getTemplateChoiceList() != null ? new ArrayList<>() : TemplateChoice.changeResponseByReview(review);
        List<String> imageUrlList = review.getImageUrl().isEmpty() ? new ArrayList<>() : JsonHelper.fromJsonToList(review.getImageUrl(), new TypeReference<>() {});


        return ReviewResponse.of(review, imageUrlList, choiceResponseList);
    }

    public Page<ReviewResponse> readByMemberId(Long memberId, Pageable pageable) {
        Page<Review> reviewPage = reviewDslRepositoryImpl.findByMemberIdWithPagination(memberId, pageable);

        return reviewPage.map(review -> {
            List<String> imageUrlList = review.getImageUrl().isEmpty() ? new ArrayList<>()
                    : JsonHelper.fromJsonToList(review.getImageUrl(), new TypeReference<>() {
            });

            List<TemplateChoiceResponse> choiceResponseList = TemplateChoice.changeResponseByReview(review);

            return ReviewResponse.of(review, imageUrlList, choiceResponseList);
        });
    }

    public List<ReviewResponse> readByProductIdWithSearch(Long productId, String keyWord, Integer rating, Pageable pageable) {

        List<Long> resultList = elasticSearchQueryClient.searchByCondition(keyWord, productId, rating, pageable);

        List<ReviewResponse> reviewList = reviewDslRepositoryImpl.findByIdInWithPagination(resultList).stream().map(review -> {
            List<String> imageUrlList = review.getImageUrl().isEmpty() ? new ArrayList<>()
                    : JsonHelper.fromJsonToList(review.getImageUrl(), new TypeReference<>() {
            });

            List<TemplateChoiceResponse> choiceResponseList = TemplateChoice.changeResponseByReview(review);

            return ReviewResponse.of(review, imageUrlList, choiceResponseList);
        }).toList();

        return reviewList;
    }


    @Transactional
    public ReviewResponse updateReview(Long memberId, Long reviewId, UpdateReviewRequest request) {

        Member member = memberRepository
                .findById(memberId).orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Review review = findByIdElseThrow(reviewId);

        if (!member.getId().equals(review.getMember().getId())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN);
        }

        String dbImageList = JsonHelper.toJson(request.imageUrlList());
        review.update(request.title(), request.content(), request.rating(), dbImageList);

        List<TemplateChoiceResponse> choiceResponseList = review.getTemplateChoiceList().stream()
                .map(c -> {
                    UpdateTemplateChoiceRequest updateRequest = request.templateChoiceList().stream()
                            .filter(r -> r.templateChoiceId().equals(c.getId()))
                            .findFirst().orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_REVIEW_TEMPLATE_NOT_FOUND));

                    c.update(TemplateChoiceGrade.of(updateRequest.answer()));
                    ReviewTemplate reviewTemplate = c.getProductReviewTemplate().getReviewTemplate();
                    return TemplateChoiceResponse.of(reviewTemplate.getQuestion(), reviewTemplate.getChoice(c.getChoseAnswer()));
                })
                .toList();

        elasticSearchQueryClient.updateReviewDocument(reviewId, request);

        return ReviewResponse.of(review, request.imageUrlList(), choiceResponseList);
    }

    @Transactional
    public void deleteReview(Long memberId, Long reviewId) {

        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Member member = memberRepository.getReferenceById(memberId);

        if (!reviewRepository.existsById(reviewId)) {
            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }
        Review review = reviewRepository.getReferenceById(reviewId);

        if (!member.getId().equals(review.getMember().getId())) {
            throw new RoleMismatchException(ErrorCode.FORBIDDEN);
        }

        review.delete();
        elasticSearchQueryClient.deleteReviewDocument(reviewId);
    }

    private Review findByIdElseThrow(Long reviewId) {
        return reviewDslRepositoryImpl.findByIdWithChoice(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
    }

}
