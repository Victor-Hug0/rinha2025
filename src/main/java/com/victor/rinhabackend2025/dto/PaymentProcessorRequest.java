package com.victor.rinhabackend2025.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentProcessorRequest(
        @NotNull
        UUID correlationId,
        @NotNull @Positive
        BigDecimal amount,
        @NotNull
        Instant processedAt
) {
}
