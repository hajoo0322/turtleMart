package turtleMart.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import turtleMart.payment.dto.request.PaymentRequest;
import turtleMart.payment.service.EmailService;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/payment-confirmation")
    public ResponseEntity<Void> sendPaymentConfirmationEmail(@RequestParam Long orderId) {
        emailService.sendPaymentCompleteEmail(orderId);
        return ResponseEntity.ok().build();
    }
//
//    @PostMapping("/payment-test")
//    public ResponseEntity<Void> sendPaymentConfirmationEmail(@RequestBody PaymentRequest paymentRequest) {
//        emailService.sendPaymentCompleteEmail(orderId);
//        return ResponseEntity.ok().build();
//    }
}
