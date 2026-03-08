package com.api_order.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemRequest {

    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}
