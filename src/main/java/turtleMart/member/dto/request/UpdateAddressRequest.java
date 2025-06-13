package turtleMart.member.dto.request;

public record UpdateAddressRequest(
        String addressName,
        String receiverName,
        String address,
        String detailAddress,
        String receiverPhone,
        String shippingRequirement
) {
}
