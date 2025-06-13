package turtleMart.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turtleMart.global.common.BaseEntity;
import turtleMart.member.dto.request.AddressRegisterRequest;
import turtleMart.member.dto.request.UpdateAddressRequest;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    private String addressName;

    private String receiverName;

    private String address;

    private String detailAddress;

    private String receiverPhone;

    private String shippingRequirement;

    public Address(
            String addressName,
            String receiverName,
            String address,
            String detailAddress,
            String receiverPhone,
            String shippingRequirement
    ) {
        this.addressName = addressName;
        this.receiverName = receiverName;
        this.address = address;
        this.detailAddress = detailAddress;
        this.receiverPhone = receiverPhone;
        this.shippingRequirement = shippingRequirement;
    }

    public static Address of(AddressRegisterRequest request) {
        return new Address(
                request.addressName(),
                request.receiverName(),
                request.address(),
                request.detailAddress(),
                request.receiverPhone(),
                request.shippingRequirement()
        );
    }

    public void updateAddress(UpdateAddressRequest request) {
        this.addressName = request.addressName() != null ? request.addressName() : this.addressName;
        this.receiverName = request.receiverName() != null ? request.receiverName() : this.receiverName;
        this.address = request.address() != null ? request.address() : this.address;
        this.detailAddress = request.detailAddress() != null ? request.detailAddress() : this.detailAddress;
        this.receiverPhone = request.receiverPhone() != null ? request.receiverPhone() : this.receiverPhone;
        this.shippingRequirement = request.shippingRequirement() != null ? request.shippingRequirement() : this.shippingRequirement;
    }
}
