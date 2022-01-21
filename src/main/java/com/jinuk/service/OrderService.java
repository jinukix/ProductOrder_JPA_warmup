package com.jinuk.service;

import com.jinuk.domain.Order;
import com.jinuk.domain.Product;
import com.jinuk.domain.User;
import com.jinuk.dto.OrderResponseDto;
import com.jinuk.exception.NotFoundException;
import com.jinuk.repository.OrderRepository;
import com.jinuk.repository.ProductRepository;
import com.jinuk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getMyOrders(Pageable pageable) {
        Long userId = userService.getCurrentUser();
        return orderRepository.findAllByUserId(userId, pageable)
            .map(OrderResponseDto::convertOrder);
    }

    public void orderProduct(Long productId) {
        Long userId = userService.getCurrentUser();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Select not found user"));
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Select not found product"));
        orderRepository.save(new Order(user, product));
    }
}
