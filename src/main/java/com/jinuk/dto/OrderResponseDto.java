package com.jinuk.dto;

import com.jinuk.domain.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponseDto {

    private Long id;
    private ProductResponseDto product;

    public static OrderResponseDto convertOrder(Order order) {
        return OrderResponseDto.builder()
            .id(order.getId())
            .product(ProductResponseDto.convertProduct(order.getProduct()))
            .build();
    }
}
