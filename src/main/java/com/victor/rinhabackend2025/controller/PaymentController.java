package com.victor.rinhabackend2025.controller;

import com.victor.rinhabackend2025.dto.PaymentRequest;
import com.victor.rinhabackend2025.dto.PaymentSummaryResponse;
import com.victor.rinhabackend2025.service.PaymentPublisherService;
import com.victor.rinhabackend2025.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final PaymentPublisherService  paymentPublisherService;

    public PaymentController(PaymentService paymentService,  PaymentPublisherService paymentPublisherService) {
        this.paymentService = paymentService;
        this.paymentPublisherService = paymentPublisherService;
    }

    @PostMapping("/payments")
    public ResponseEntity<Void> createPayment(@RequestBody @Valid PaymentRequest paymentRequest) {

        try {
            paymentPublisherService.publishPayment(paymentRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/payments-summary")
    public ResponseEntity<PaymentSummaryResponse> getPaymentSummary(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime to) {

        PaymentSummaryResponse paymentSummary = paymentService.getPaymentSummary(from, to);

        log.info("Payment summary: {}", paymentSummary);
        return ResponseEntity.ok(paymentSummary);
    }



}
