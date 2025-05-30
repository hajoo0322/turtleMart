package turtleMart.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import turtleMart.order.dto.response.RefundApplyResultResponse;
import turtleMart.order.dto.response.RefundResponse;
import turtleMart.order.service.RefundService;
import turtleMart.order.service.RefundWaiter;

import java.util.List;

@RestController
@RequestMapping("/orders/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;
    private final RefundWaiter refundWaiter;

    @PostMapping("/{orderItemId}")
    public ResponseEntity<RefundApplyResultResponse> applyRefund(
            @PathVariable Long orderItemId
            /*TODO 토큰에서 회원 ID 가져오기*/
    ) {
        RefundApplyResultResponse response = refundService.applyRefund(1L, orderItemId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // TODO 판매자 권한 부여
    @GetMapping()
    public ResponseEntity<List<RefundResponse>> getRefundRequestList(
            /*TODO 토큰에서 판매자 ID 가져오기*/
    ) {
        List<RefundResponse> responseList = refundService.getRefundRequestList(1L);

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PatchMapping("/{orderItemId}")
    public DeferredResult<ResponseEntity<Void>> approveRefund(
            @PathVariable Long orderItemId
            /*TODO 토큰에서 판매자 ID 가져오기*/
    ){
        DeferredResult<ResponseEntity<Void>> result = refundWaiter.createWaiter(orderItemId);
        refundService.approveOrderItemRefund(orderItemId, 1L);

        return result;
    }
}
