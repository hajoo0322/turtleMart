package turtleMart.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.global.exception.*;
import turtleMart.global.kafka.dto.OperationWrapperDto;
import turtleMart.global.kafka.enums.OperationType;
import turtleMart.global.slack.SlackNotifier;
import turtleMart.global.utill.JsonHelper;
import turtleMart.member.entity.Seller;
import turtleMart.member.repository.SellerRepository;
import turtleMart.order.entity.OrderItem;
import turtleMart.order.repository.OrderItemRepository;
import turtleMart.product.dto.ProductOptionCombinationDeleteDto;
import turtleMart.product.dto.ProductOptionCombinationInventoryDto;
import turtleMart.product.dto.ProductOptionCombinationPriceDto;
import turtleMart.product.dto.ProductOptionCombinationStatusDto;
import turtleMart.product.dto.request.ProductOptionCombinationRequest;
import turtleMart.product.dto.response.DuplicateList;
import turtleMart.product.dto.response.ProductOptionCombinationResponse;
import turtleMart.product.dto.response.ProductOptionCombinationResponseCreate;
import turtleMart.product.entity.*;
import turtleMart.product.repository.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOptionCombinationService {

    private final ProductRepository productRepository;
    private final ProductOptionMapRepository productOptionMapRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;
    private final ProductOptionCombinationDslRepository productOptionCombinationDslRepository;
    private final OrderItemRepository orderItemRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, Boolean> redisTemplate;
    private final ProductDslRepository productDslRepository;
    private final SellerRepository sellerRepository;
    private final SlackNotifier slackNotifier;

    @Value("${server.id}")
    private String serverId;

    @Value("${kafka.topic.order}")
    private String orderTopic;

    @Transactional
    public ProductOptionCombinationResponseCreate createProductOptionCombination(List<ProductOptionCombinationRequest> productOptionCombinationRequest, Long memberId, Long productId) {
        List<ProductOptionCombination> productOptionCombinationList = new ArrayList<>();
        List<String> duplicated = new ArrayList<>();
        Product product = productDslRepository.findByIdWithSeller(productId);
        Seller seller = sellerRepository.findByMemberId(memberId).orElseThrow(() -> new NotFoundException(ErrorCode.SELLER_NOT_FOUND));
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RoleMismatchException(ErrorCode.FORBIDDEN);
        }

        for (ProductOptionCombinationRequest optionCombinationRequest : productOptionCombinationRequest) {
            TreeSet<Long> valueIdSet = new TreeSet<>(optionCombinationRequest.valueIdList());
            String uniqueKey = valueIdSet.stream().map(String::valueOf).collect(Collectors.joining("-"));
            if (productOptionCombinationRepository.existsByProductIdAndUniqueKey(productId, uniqueKey)) {
                duplicated.add(uniqueKey);
                continue;
            }
            ProductOptionCombination productOptionCombination =
                    ProductOptionCombination.of(product, optionCombinationRequest.price(), optionCombinationRequest.inventory(),uniqueKey);
            List<ProductOptionValue> productOptionValueList = productOptionValueRepository.findAllById(valueIdSet.stream().toList());
            if (productOptionValueList.isEmpty()) {
                throw new NotFoundException(ErrorCode.PRODUCT_OPTION_VALUE_NOT_FOUND);
            }
            for (ProductOptionValue productOptionValue : productOptionValueList) {
                ProductOptionMap productOptionMap = ProductOptionMap.of(productOptionCombination, productOptionValue);
                productOptionCombination.addOptionMap(productOptionMap);
            }
            productOptionCombinationList.add(productOptionCombination);
        }
        DuplicateList duplicateList = DuplicateList.from(duplicated);
        productOptionCombinationRepository.saveAll(productOptionCombinationList);
        List<ProductOptionCombinationResponse> productOptionCombinationResponseList = productOptionCombinationList.stream().map(ProductOptionCombinationResponse::from).toList();

        return ProductOptionCombinationResponseCreate.of(productOptionCombinationResponseList, duplicateList);
    }

    @Transactional(readOnly = true)
    public List<ProductOptionCombinationResponse> getAllCombinationByProduct(Long productId) {
        List<ProductOptionCombination> productOptionCombinationList = productOptionCombinationDslRepository.findAllByProductIdWithMapAndValue(productId);
        return productOptionCombinationList.stream().map(ProductOptionCombinationResponse::from).toList();
    }

    @Transactional
    public void hardDeleteProductOptionCombination(Long memberId, Long productOptionCombinationId) {
        ProductOptionCombination productOptionCombination = checkPermission(memberId, productOptionCombinationId);
        if (orderItemRepository.existsByProductOptionCombinationId(productOptionCombinationId)) {
            throw new BadRequestException(ErrorCode.PRODUCT_OPTION_COMBINATION_ALL_READY_SOLD);
        }
        productOptionCombinationRepository.delete(productOptionCombination);
    }

    private ProductOptionCombination checkPermission(Long memberId, Long productOptionCombinationId) {
        ProductOptionCombination productOptionCombination = productOptionCombinationDslRepository.findByIdWithProductAndSeller(productOptionCombinationId);
        if (productOptionCombination == null) {
            throw new NotFoundException(ErrorCode.PRODUCT_OPTION_COMBINATION_NOT_FOUND);
        }
        Seller seller = sellerRepository.findByMemberId(memberId).orElseThrow(() -> new NotFoundException(ErrorCode.SELLER_NOT_FOUND));

        if (!productOptionCombination.getProduct().getSeller().getId().equals(seller.getId())) {
            throw new RoleMismatchException(ErrorCode.FORBIDDEN);
        }
        return productOptionCombination;
    }

    public String updateProductOptionCombinationPrice(Long memberId, Long productOptionCombinationId, Integer price) {
        checkPermission(memberId, productOptionCombinationId);
        String priceChangeRedisKey = "softLock:priceChange:combination:" + productOptionCombinationId;
        if (redisTemplate.hasKey(priceChangeRedisKey)) {
            throw new BadRequestException(ErrorCode.SOFT_LOCK_CANT_ACCESS);
        }
        redisTemplate.opsForValue().set(priceChangeRedisKey,false, Duration.ofMinutes(4));

        String operationId = serverId + ":" + UUID.randomUUID();
        ProductOptionCombinationPriceDto productOptionCombinationPriceDto = ProductOptionCombinationPriceDto.of(productOptionCombinationId, price,operationId, OperationType.PRICE_CHANGE);
        String payload = JsonHelper.toJson(productOptionCombinationPriceDto);

        OperationWrapperDto wrapperDto = OperationWrapperDto.from(OperationType.PRICE_CHANGE, payload);
        String value = JsonHelper.toJson(wrapperDto);

        kafkaTemplate.send("order_make_topic", productOptionCombinationId.toString(), value);

        return operationId;
    }

    public String updateProductOptionCombinationInventory(Long memberId, Long productOptionCombinationId, Integer inventory) {
        checkPermission(memberId, productOptionCombinationId);
        String operationId = serverId + ":" + UUID.randomUUID();
        ProductOptionCombinationInventoryDto productOptionCombinationInventoryDto =
                ProductOptionCombinationInventoryDto.of(productOptionCombinationId, inventory, operationId, OperationType.INVENTORY_UPDATE);
        String payload = JsonHelper.toJson(productOptionCombinationInventoryDto);
        kafkaTemplate.send("", productOptionCombinationId.toString(), payload);
        return operationId;
    }

    public String updateProductOptionCombinationInventoryOverride(Long memberId, Long productOptionCombinationId, Integer inventory) {
        checkPermission(memberId, productOptionCombinationId);
        String operationId = serverId + ":" + UUID.randomUUID();
        ProductOptionCombinationInventoryDto productOptionCombinationInventoryDto =
                ProductOptionCombinationInventoryDto.of(productOptionCombinationId, inventory, operationId, OperationType.INVENTORY_OVERRIDE);
        String payload = JsonHelper.toJson(productOptionCombinationInventoryDto);
        kafkaTemplate.send("", productOptionCombinationId.toString(), payload);
        return operationId;
    }

    public String softDeleteProductOptionCombination(Long memberId, Long productOptionCombinationId) {
        checkPermission(memberId, productOptionCombinationId);
        String operationId = serverId + ":" + UUID.randomUUID();
        ProductOptionCombinationDeleteDto productOptionCombinationDeleteDto =
                ProductOptionCombinationDeleteDto.of(productOptionCombinationId, operationId, OperationType.COMBINATION_DELETE);
        String payload = JsonHelper.toJson(productOptionCombinationDeleteDto);
        kafkaTemplate.send("", productOptionCombinationId.toString(), payload);
        return operationId;
    }

    public String updateProductOptionCombinationStatus(Long memberId, Long productOptionCombinationId, CombinationStatus combinationStatus) {
        checkPermission(memberId, productOptionCombinationId);
        String operationId = serverId + ":" + UUID.randomUUID();
        ProductOptionCombinationStatusDto productOptionCombinationStatusDto = ProductOptionCombinationStatusDto.of(productOptionCombinationId, operationId, combinationStatus);
        String payload = JsonHelper.toJson(productOptionCombinationStatusDto);
        kafkaTemplate.send("", productOptionCombinationId.toString(), payload);
        return operationId;
    }

    @Transactional
    public void decreaseProductOptionCombinationInventory(Long orderId) {
        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(orderId);

        for (OrderItem orderItem : orderItemList) {
            Long productOptionCombinationId = orderItem.getProductOptionCombination().getId();
            Integer quantity = orderItem.getQuantity();

            ProductOptionCombination productOptionCombination = productOptionCombinationRepository
                .findByIdWithPessimisticLock(productOptionCombinationId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_OPTION_COMBINATION_NOT_FOUND));

            if (productOptionCombination.getInventory() < quantity) {
                slackNotifier.sendInventoryDecreaseFailureAlert(
                    orderId,
                    orderItem.getOrder().getMember().getName(),
                    productOptionCombination.getProduct().getId().toString(),
                    productOptionCombination.getProduct().getName(),
                    ErrorCode.PRODUCT_OPTION_COMBINATION_OUT_OF_INVENTORY.getMessage());

                throw new ConflictException(ErrorCode.PRODUCT_OPTION_COMBINATION_OUT_OF_INVENTORY);
            }

            productOptionCombination.decreaseInventory(quantity);
        }
    }

    @Transactional
    public void restoreProductOptionCombinationInventory(Long orderId) {
        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderId(orderId);

        for (OrderItem orderItem : orderItemList) {
            Long productOptionCombinationId = orderItem.getProductOptionCombination().getId();
            Integer quantity = orderItem.getQuantity();

            ProductOptionCombination productOptionCombination = productOptionCombinationRepository
                .findByIdWithPessimisticLock(productOptionCombinationId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_OPTION_COMBINATION_NOT_FOUND));

            productOptionCombination.increaseInventory(quantity);

            log.info("✅ [{}]의 재고가 {}개 복원되었습니다! (OptionCombinationId: {})", orderItem.getName(), quantity, productOptionCombinationId);
        }
        log.info("✅ 주문 ID {}의 모든 재고 복원 작업이 완료되었습니다.", orderId);
    }

    @Transactional
    public void changePrice(ProductOptionCombinationPriceDto priceDto, OperationWrapperDto wrapperDto, String key) {
        Long combinationId = priceDto.productOptionCombinationId();
        Integer newPrice = priceDto.price();
        String lockKey = "softLock:priceChange:combination:" + combinationId;

        ProductOptionCombination combination = productOptionCombinationRepository.findById(combinationId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_OPTION_COMBINATION_NOT_FOUND));
        Integer currentPrice = combination.getPrice();

        // 가격 변경
        if (!newPrice.equals(currentPrice)) {
            combination.updatePrice(newPrice);
            log.info("✅ 가격 변경 감지 및 반영 완료: combinationId={}", combinationId);
        } else {
            log.info("❌ 가격 변동 없음: combinationId={}", combinationId);
        }

        // soft Lock 해제
        redisTemplate.delete(lockKey);
        log.info("🔓 soft lock 해제 완료: {}", lockKey);

        redisTemplate.opsForValue().set("status:" + priceDto.operationId(), true, Duration.ofMinutes(5));
    }
}
