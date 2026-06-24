package com.enterprise.order.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "product_id", nullable = false, unique = true, length = 50)
    private String productId;

    @Column(name = "stock_count", nullable = false)
    private Integer stockCount;

    public Inventory() {
    }

    public Inventory(String id, String productId, Integer stockCount) {
        this.id = id;
        this.productId = productId;
        this.stockCount = stockCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }
}
