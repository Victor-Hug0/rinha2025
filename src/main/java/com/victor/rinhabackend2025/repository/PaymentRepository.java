package com.victor.rinhabackend2025.repository;

import com.victor.rinhabackend2025.dto.ProcessorSummaryDTO;
import com.victor.rinhabackend2025.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
    SELECT new com.victor.rinhabackend2025.dto.ProcessorSummaryDTO(
      p.processor, count(p), sum(p.amount))
    FROM Payment p
    WHERE p.requestedAt >= :from
      AND p.requestedAt <= :to
    GROUP BY p.processor
    """)
    List<ProcessorSummaryDTO> summarizeByProcessor(Instant from, Instant to);

    @Query("""
    SELECT new com.victor.rinhabackend2025.dto.ProcessorSummaryDTO(
      p.processor, count(p), sum(p.amount))
    FROM Payment p
    GROUP BY p.processor
    """)
    List<ProcessorSummaryDTO> summarizeByProcessorAll();

}
