package turtleMart.delivery.dto.response;

import turtleMart.delivery.entity.Courier;
import turtleMart.delivery.entity.Sender;

import java.time.LocalDateTime;

public record SenderResponse(
    Long id,
    CourierInfo courier,
    String name,
    String phoneNumber,
    String address,
    String detailAddress,
    LocalDateTime createdAt
) {
    public static SenderResponse from(Sender sender) {
        return new SenderResponse(
            sender.getId(),
            CourierInfo.from(sender.getCourier()),
            sender.getName(),
            sender.getPhoneNumber(),
            sender.getAddress(),
            sender.getDetailAddress(),
            sender.getCreatedAt()
        );
    }

    public record CourierInfo(
        Long id,
        String name,
        String code
    ) {
        public static CourierInfo from(Courier courier) {
            return new CourierInfo(courier.getId(), courier.getName(), courier.getCode());
        }
    }
}
