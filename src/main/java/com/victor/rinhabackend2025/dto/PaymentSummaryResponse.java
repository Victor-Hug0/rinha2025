package com.victor.rinhabackend2025.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.victor.rinhabackend2025.entity.PaymentSummary;

public record PaymentSummaryResponse(
        @JsonProperty("default")
        PaymentSummary xdefault,
        PaymentSummary fallback
) {
}
