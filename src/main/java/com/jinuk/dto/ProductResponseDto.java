package com.jinuk.dto;

import com.jinuk.domain.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {

    private Long id;
    private String name;
    private Long price;

    public static ProductResponseDto convertProduct(Product product) {
        return ProductResponseDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .build();
    }
}
