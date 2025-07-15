package com.victor.rinhabackend2025.dto;

import java.math.BigDecimal;

public record ProcessorSummaryDTO(
        String processor,
        Long totalRequests,
        BigDecimal totalAmount
) {
}
