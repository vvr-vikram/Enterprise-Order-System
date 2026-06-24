package com.enterprise.order.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {
    private String id;
    private String userId;
    private String status;
    private BigDecimal total;
    private String idempotencyKey;
    private LocalDateTime createdAt;

    public OrderResponse() {
    }

    public OrderResponse(String id, String userId, String status, BigDecimal total, String idempotencyKey, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.total = total;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
