package turtleMart.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.delivery.dto.reqeust.CreateDeliveryRequest;
import turtleMart.delivery.dto.reqeust.UpdateDeliveryRequest;
import turtleMart.delivery.dto.reqeust.UpdateDeliveryStatusRequest;
import turtleMart.delivery.dto.response.CreateDeliveryResponse;
import turtleMart.delivery.dto.response.ReadDeliveryResponse;
import turtleMart.delivery.dto.response.UpdateDeliveryResponse;
import turtleMart.delivery.entity.Delivery;
import turtleMart.delivery.entity.DeliveryStatus;
import turtleMart.delivery.entity.Sender;
import turtleMart.member.repository.AddressRepository;
import turtleMart.delivery.repository.DeliveryRepository;
import turtleMart.delivery.repository.SenderRepository;
import turtleMart.global.exception.BadRequestException;
import turtleMart.global.exception.ErrorCode;
import turtleMart.global.exception.NotFoundException;
import turtleMart.member.entity.Address;
import turtleMart.member.entity.Seller;
import turtleMart.member.repository.SellerRepository;
import turtleMart.order.entity.Order;
import turtleMart.order.repository.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final SellerRepository sellerRepository;
    private final SenderRepository senderRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public CreateDeliveryResponse createDelivery(CreateDeliveryRequest request) {
        if (!orderRepository.existsById(request.orderId())) {
            throw new NotFoundException(ErrorCode.ORDER_NOT_FOUND);
        }
        if (!sellerRepository.existsById(request.sellerId())) {
            throw new NotFoundException(ErrorCode.SELLER_NOT_FOUND);
        }
        if (!senderRepository.existsById(request.senderId())) {
            throw new NotFoundException(ErrorCode.SENDER_NOT_FOUND);
        }
        Order order = orderRepository.getReferenceById(request.orderId());
        Seller seller = sellerRepository.getReferenceById(request.sellerId());
        Sender sender = senderRepository.getReferenceById(request.senderId());

        Address address = addressRepository.findById(request.addressId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

        Delivery delivery = Delivery.of(order, seller, sender, address, request.deliveryRequest());

        deliveryRepository.save(delivery);

        return CreateDeliveryResponse.from(delivery);
    }

    @Transactional
    public UpdateDeliveryResponse updateTrackingNumber(Long deliveryId, UpdateDeliveryRequest request) {
        Delivery delivery = getDelivery(deliveryId);

        delivery.updateTrackingNumber(request.trackingNumber());

        return UpdateDeliveryResponse.from(delivery);
    }

    public ReadDeliveryResponse readDelivery(Long deliveryId) {
        Delivery delivery = getDelivery(deliveryId);

        return ReadDeliveryResponse.from(delivery);
    }

    public List<ReadDeliveryResponse> readAllDeliveriesByMember(Long memberId) {
        List<Delivery> deliveryList = deliveryRepository.findAllByMemberId(memberId);

        return deliveryList.stream()
            .map(ReadDeliveryResponse::from)
            .toList();
    }

    public List<ReadDeliveryResponse> readAllDeliveriesBySeller(Long sellerId) {
        List<Delivery> deliveryList = deliveryRepository.findAllBySeller(sellerId);

        return deliveryList.stream()
            .map(ReadDeliveryResponse::from)
            .toList();
    }

    @Transactional
    public UpdateDeliveryResponse updateDeliveryStatus(Long deliveryId, UpdateDeliveryStatusRequest request) {
        Delivery delivery = getDelivery(deliveryId);

        if (!delivery.getDeliveryStatus().canTransitionTo(request.deliveryStatus())) {
            throw new BadRequestException(ErrorCode.INVALID_DELIVERY_STATUS);
        }

        if (request.deliveryStatus() == DeliveryStatus.DELIVERED) {
            delivery.updateDelivered(request.deliveryStatus());
        }

        delivery.updateDeliveryStatus(request.deliveryStatus());

        return UpdateDeliveryResponse.from(delivery);
    }

    private Delivery getDelivery(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.DELIVERY_NOT_FOUND));
    }
}
