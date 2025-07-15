package com.victor.rinhabackend2025.controller;

import com.victor.rinhabackend2025.dto.PaymentRequest;
import com.victor.rinhabackend2025.dto.PaymentSummaryResponse;
import com.victor.rinhabackend2025.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
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

    @GetMapping("/payments-summary")
    public ResponseEntity<PaymentSummaryResponse> getPaymentSummary(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to) {

        Instant fromInstant = parseInstantWithZ(from);
        Instant toInstant = parseInstantWithZ(to);

        PaymentSummaryResponse paymentSummary = paymentService.getPaymentSummary(fromInstant, toInstant);

        log.info("Payment summary: {}", paymentSummary);
        return ResponseEntity.ok(paymentSummary);
    }

    private Instant parseInstantWithZ(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (!value.endsWith("Z") && !value.matches(".*[+-][0-9]{2}:[0-9]{2}$")) {
            value = value + "Z";
        }
        return Instant.parse(value);
    }

}
