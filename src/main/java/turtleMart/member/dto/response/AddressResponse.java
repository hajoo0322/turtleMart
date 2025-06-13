package turtleMart.member.dto.response;

import turtleMart.member.entity.Address;

public record AddressResponse(
        Long addressId,
        String addressName,
        String receiverName,
        String address,
        String detailAddress,
        String receiverPhone,
        String shippingRequirement
) {
    public static AddressResponse from(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getAddressName(),
                address.getReceiverName(),
                address.getAddress(),
                address.getDetailAddress(),
                address.getReceiverPhone(),
                address.getShippingRequirement()
        );
    }
}
