package com.victor.rinhabackend2025.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        @NotNull
        UUID correlationId,
        @NotNull @Positive
        BigDecimal amount
) {
}
