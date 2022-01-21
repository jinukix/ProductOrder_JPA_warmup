package com.jinuk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jinuk.domain.Order;
import com.jinuk.domain.Product;
import com.jinuk.domain.User;
import com.jinuk.exception.NotFoundException;
import com.jinuk.repository.OrderRepository;
import com.jinuk.repository.ProductRepository;
import com.jinuk.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    private Product createProduct() {
        return Product.builder()
            .id(1L)
            .name("Product 1")
            .price(1000L)
            .build();
    }

    private Order createOrder() {
        return Order.builder()
            .id(1L)
            .user(User.builder().build())
            .product(createProduct())
            .build();
    }

    private Page<Order> getOrdersPage() {
        List<Order> orders = new ArrayList<>();
        orders.add(createOrder());
        return new PageImpl<>(orders, PageRequest.of(0, 10), 1);
    }

    @Test
    @DisplayName("주문 목록 조회에 성공합니다.")
    public void getMyOrdersTestWhenSuccess() {
        when(userService.getCurrentUser()).thenReturn(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orders = getOrdersPage();
        when(orderRepository.findAllByUserId(1L, pageable)).thenReturn(orders);

        var result = orderService.getMyOrders(pageable);
        assertEquals(result.getTotalElements(), 1L);
        assertEquals(result.getContent().get(0).getId(), 1L);
        assertEquals(result.getContent().get(0).getProduct().getId(), 1L);
        assertEquals(result.getContent().get(0).getProduct().getName(), "Product 1");
        assertEquals(result.getContent().get(0).getProduct().getPrice(), 1000L);

        verify(userService).getCurrentUser();
        verify(orderRepository).findAllByUserId(1L, pageable);
    }

    @Test
    @DisplayName("상품 주문에 성공합니다.")
    public void orderProductTestWhenSuccess() {
        User user = User.builder().build();
        Product product = Product.builder().build();

        when(userService.getCurrentUser()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        orderService.orderProduct(2L);

        verify(userService).getCurrentUser();
        verify(userRepository).findById(1L);
        verify(productRepository).findById(2L);
        verify(orderRepository).save(any());
    }

    @Test
    @DisplayName("상품 주문에 실패합니다. :존재하지 않는 유저입니다.")
    public void orderProductTestWhenFail1() {
        when(userService.getCurrentUser()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.orderProduct(2L));
    }

    @Test
    @DisplayName("상품 주문에 실패합니다. :존재하지 않는 상품입니다.")
    public void orderProductTestWhenFail2() {
        User user = User.builder().build();

        when(userService.getCurrentUser()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.orderProduct(2L));
    }

}