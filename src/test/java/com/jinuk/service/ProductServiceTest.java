package com.jinuk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jinuk.domain.Product;
import com.jinuk.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product createProduct() {
        return Product.builder()
            .id(1L)
            .name("Product 1")
            .price(1000L)
            .build();
    }

    private Page<Product> getProductsPage() {
        List<Product> products = new ArrayList<>();
        products.add(createProduct());
        return new PageImpl<>(products, PageRequest.of(0, 10), 1);
    }

    @Test
    @DisplayName("상품 목록 조회에 성공합니다.")
    public void getProductsTestWhenSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = getProductsPage();
        when(productRepository.findAll(pageable)).thenReturn(products);
        var result = productService.getProducts(pageable);

        assertEquals(result.getTotalElements(), 1L);
        assertEquals(result.getContent().get(0).getId(), 1L);
        assertEquals(result.getContent().get(0).getName(), "Product 1");
        assertEquals(result.getContent().get(0).getPrice(), 1000L);

        verify(productRepository).findAll(pageable);
    }
}