package turtleMart.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.delivery.dto.reqeust.CreateCourierRequest;
import turtleMart.delivery.dto.reqeust.UpdateCourierRequest;
import turtleMart.delivery.dto.response.CreateCourierResponse;
import turtleMart.delivery.dto.response.ReadCourierResponse;
import turtleMart.delivery.dto.response.UpdateCourierResponse;
import turtleMart.delivery.entity.Courier;
import turtleMart.delivery.repository.CourierRepository;
import turtleMart.delivery.repository.SenderRepository;
import turtleMart.global.exception.ConflictException;
import turtleMart.global.exception.ErrorCode;
import turtleMart.global.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final SenderRepository senderRepository;

    @Transactional
    public CreateCourierResponse createCourier(CreateCourierRequest request) {
        if (courierRepository.existsByNameAndCode(request.name(), request.code())) {
            throw new ConflictException(ErrorCode.COURIER_ALREADY_EXISTS);
        }

        Courier courier = Courier.of(request.name(), request.code(), request.trackingUrlTemplate());

        courierRepository.save(courier);

        return CreateCourierResponse.from(courier);
    }

    public List<ReadCourierResponse> readAllCouriers() {
        List<Courier> courierList = courierRepository.findAllByIsDeletedFalse();

        return courierList.stream()
            .map(ReadCourierResponse::from)
            .toList();
    }

    @Transactional
    public UpdateCourierResponse updateCourier(UpdateCourierRequest request, Long courierId) {
        Courier courier = courierRepository.findById(courierId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.COURIER_NOT_FOUND));

        courier.update(request);

        return UpdateCourierResponse.from(courier);
    }

    @Transactional
    public void deleteCourier(Long courierId) {
        Courier courier = courierRepository.findById(courierId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.COURIER_NOT_FOUND));

        long senderCount = senderRepository.countByCourier(courier);

        if (senderCount > 0) {
            throw new ConflictException(ErrorCode.COURIER_DELETE_FAILED);
        }

        courier.delete();
    }
}
