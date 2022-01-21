package com.jinuk.controller;

import com.jinuk.annotation.LoginCheck;
import com.jinuk.dto.OrderResponseDto;
import com.jinuk.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orders")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @LoginCheck
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(final Pageable pageable) {
        Page<OrderResponseDto> orders = orderService.getMyOrders(pageable);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
