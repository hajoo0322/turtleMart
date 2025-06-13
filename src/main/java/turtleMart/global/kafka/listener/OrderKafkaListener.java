package turtleMart.global.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import turtleMart.global.exception.NotFoundException;
import turtleMart.global.kafka.dto.OperationWrapperDto;
import turtleMart.global.utill.JsonHelper;
import turtleMart.order.dto.request.OrderWrapperRequest;
import turtleMart.order.service.OrderService;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderKafkaListener {

    private final OrderService orderService;

    @KafkaListener(topics = "${kafka.topic.order.create}", groupId = "order-group")
    public void listenOrderCreate(@Header(KafkaHeaders.RECEIVED_KEY) String key, String value) {

        OperationWrapperDto wrapperDto = JsonHelper.fromJson(value, OperationWrapperDto.class);
        String payload = wrapperDto.payload();
        OrderWrapperRequest orderWrapperRequest = JsonHelper.fromJson(payload, OrderWrapperRequest.class);

        try {
            orderService.createOrder(orderWrapperRequest);
        } catch (NotFoundException e) {
            log.error("잘못된 주문 요청 - 필수 데이터 없음(재처리 x). key={}, message={}", key, e.getMessage());
            // 필수 정보 누락으로 인한 예외라 재처리 무의미, 타임아웃 될 예정
        } catch (Exception e) {
            log.error("Kafka 주문 생성 처리 중 예기치 못한 오류 발생. key={}, value={}", key, value, e);
        }
    }
}
