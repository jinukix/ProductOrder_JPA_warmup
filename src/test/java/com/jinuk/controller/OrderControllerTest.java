package com.jinuk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.jinuk.dto.OrderResponseDto;
import com.jinuk.dto.ProductResponseDto;
import com.jinuk.service.OrderService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(OrderController.class)
@MockBean(JpaMetamodelMappingContext.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(sharedHttpSession())
            .build();
    }

    private OrderResponseDto createOrder() {
        ProductResponseDto product = ProductResponseDto.builder()
            .id(1L)
            .name("Product 1")
            .price(1L)
            .build();

        return OrderResponseDto.builder()
            .id(1L)
            .product(product)
            .build();
    }

    private Page<OrderResponseDto> getOrdersPage() {
        List<OrderResponseDto> products = new ArrayList<>();
        products.add(createOrder());
        return new PageImpl<>(products, PageRequest.of(0, 10), 1);
    }

    @Test
    @DisplayName("주문목록 조회에 성공합니다.")
    public void getProductsTestWhenSuccess() throws Exception {
        Page<OrderResponseDto> orders = getOrdersPage();
        given(orderService.getMyOrders(any())).willReturn(orders);

        mockMvc.perform(get("/orders")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("orders/get", responseFields(
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER)
                    .description("조회한 주문의 ID"),
                fieldWithPath("content.[].product.id").type(JsonFieldType.NUMBER)
                    .description("조회한 상품의 ID"),
                fieldWithPath("content.[].product.name").type(JsonFieldType.STRING)
                    .description("조회한 상품의 이름"),
                fieldWithPath("content.[].product.price").type(JsonFieldType.NUMBER)
                    .description("조회한 상품의 가격"),
                fieldWithPath("pageable.offset").ignored(),
                fieldWithPath("pageable.pageSize").ignored(),
                fieldWithPath("pageable.pageNumber").ignored(),
                fieldWithPath("pageable.paged").ignored(),
                fieldWithPath("pageable.unpaged").ignored(),
                fieldWithPath("pageable.sort.sorted").ignored(),
                fieldWithPath("pageable.sort.unsorted").ignored(),
                fieldWithPath("pageable.sort.empty").ignored(),
                fieldWithPath("sort.empty").ignored(),
                fieldWithPath("sort.sorted").ignored(),
                fieldWithPath("sort.unsorted").ignored(),
                fieldWithPath("totalPages").ignored(),
                fieldWithPath("size").ignored(),
                fieldWithPath("number").ignored(),
                fieldWithPath("first").ignored(),
                fieldWithPath("last").ignored(),
                fieldWithPath("numberOfElements").ignored(),
                fieldWithPath("empty").ignored(),
                fieldWithPath("totalElements").ignored()
            )));
    }
}