package com.enterprise.order.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(String orderId, BigDecimal amount, String productId, Integer quantity) {
        log.info("Processing payment for order {} with amount {}, product {}, quantity {}", orderId, amount, productId, quantity);

        // Idempotency Check
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment.isPresent()) {
            log.info("Payment already processed for order {}. Status: {}", orderId, existingPayment.get().getStatus());
            return existingPayment.get();
        }

        // Simulate payment rules (fail if amount <= 0 or > 10000)
        boolean isSuccess = amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(new BigDecimal("10000.00")) <= 0;

        if (!isSuccess) {
            String reason = amount.compareTo(BigDecimal.ZERO) <= 0 ? "Amount must be positive" : "Limit exceeded";
            log.warn("Payment failed for order {}: {}", orderId, reason);
            throw new IllegalStateException("Payment failed: " + reason);
        }

        String paymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(
                paymentId,
                orderId,
                "SUCCESS",
                amount,
                LocalDateTime.now()
        );

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment successfully processed for order {}. Payment ID: {}", orderId, paymentId);
        return savedPayment;
    }

    @Transactional
    public void refundPayment(String orderId) {
        log.info("Compensating Transaction: Attempting to refund payment for order {}", orderId);
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            if ("SUCCESS".equals(payment.getStatus())) {
                payment.setStatus("REFUNDED");
                paymentRepository.save(payment);
                log.info("Compensating Transaction: Payment for order {} successfully refunded!", orderId);
            }
        });
    }
}
