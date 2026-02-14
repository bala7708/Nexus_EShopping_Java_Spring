package com.ecommerce.model;

import java.math.BigDecimal;
import java.io.Serializable;

public class CartItem implements Serializable {

    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
    private String imageUrl;

    public CartItem() {}

    public CartItem(Long productId, String productName, BigDecimal unitPrice, int quantity, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long v) { this.productId = v; }
    public String getProductName() { return productName; }
    public void setProductName(String v) { this.productName = v; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal v) { this.unitPrice = v; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int v) { this.quantity = v; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String v) { this.imageUrl = v; }
}
