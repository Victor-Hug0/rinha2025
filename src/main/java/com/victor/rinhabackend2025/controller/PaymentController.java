package com.victor.rinhabackend2025.controller;

import com.victor.rinhabackend2025.dto.PaymentRequest;
import com.victor.rinhabackend2025.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<Void> createPayment(@RequestBody @Valid PaymentRequest paymentRequest) {

        try {
            paymentService.processPayment(paymentRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
