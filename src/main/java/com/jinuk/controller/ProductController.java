package com.jinuk.controller;

import com.jinuk.annotation.LoginCheck;
import com.jinuk.dto.ProductResponseDto;
import com.jinuk.service.OrderService;
import com.jinuk.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/products")
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;
    private final OrderService orderService;

    @LoginCheck
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getProducts(final Pageable pageable) {
        Page<ProductResponseDto> products = productService.getProducts(pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @LoginCheck
    @PostMapping("/{productId}/orders")
    public ResponseEntity<Void> orderProduct(@PathVariable("productId") Long productId) {
        orderService.orderProduct(productId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
