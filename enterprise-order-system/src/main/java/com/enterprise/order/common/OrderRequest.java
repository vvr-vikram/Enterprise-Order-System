package com.enterprise.order.common;

import java.math.BigDecimal;

public class OrderRequest {
    private String userId;
    private BigDecimal total;
    private String productId;
    private Integer quantity;

    public OrderRequest() {
    }

    public OrderRequest(String userId, BigDecimal total, String productId, Integer quantity) {
        this.userId = userId;
        this.total = total;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
