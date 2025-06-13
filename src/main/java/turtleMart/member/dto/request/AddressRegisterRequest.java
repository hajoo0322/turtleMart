package turtleMart.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddressRegisterRequest(
        String addressName,
        @NotBlank(message = "이름을 입력해주세요.")
        String receiverName,
        @NotBlank(message = "주소를 입력해주세요.")
        String address,
        @NotBlank(message = "상세주소를 입력해주세요.")
        String detailAddress,
        String receiverPhone,
        String shippingRequirement
) {
}
