package com.jinuk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.jinuk.dto.ProductResponseDto;
import com.jinuk.exception.NotFoundException;
import com.jinuk.service.OrderService;
import com.jinuk.service.ProductService;
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
@WebMvcTest(ProductController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

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

    private ProductResponseDto createProduct() {
        return ProductResponseDto.builder()
            .id(1L)
            .name("Product 1")
            .price(1L)
            .build();
    }

    private Page<ProductResponseDto> getProductsPage() {
        List<ProductResponseDto> products = new ArrayList<>();
        products.add(createProduct());
        return new PageImpl<>(products, PageRequest.of(0, 10), 1);
    }

    @Test
    @DisplayName("???????????? ????????? ???????????????.")
    public void getProductsTestWhenSuccess() throws Exception {
        Page<ProductResponseDto> products = getProductsPage();
        given(productService.getProducts(any())).willReturn(products);

        mockMvc.perform(get("/products")
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("products/get", responseFields(
                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER)
                    .description("????????? ????????? ID"),
                fieldWithPath("content.[].name").type(JsonFieldType.STRING)
                    .description("????????? ????????? ??????"),
                fieldWithPath("content.[].price").type(JsonFieldType.NUMBER)
                    .description("????????? ????????? ??????"),
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

    @Test
    @DisplayName("??????????????? ???????????????.")
    public void orderProductTestWhenSuccess() throws Exception {
        Long productId = 1L;
        doNothing().when(orderService).orderProduct(productId);

        mockMvc.perform(post("/products/{productId}/orders", productId)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("products/{productId}/orders/successful", pathParameters(
                parameterWithName("productId").description("????????? ????????? ID")
            )));
    }

    @Test
    @DisplayName("??????????????? ???????????????. :???????????? ?????? ???????????????.")
    public void orderProductTestWhenFail1() throws Exception {
        Long productId = 1L;

        doThrow(new NotFoundException("Select not found user.")).when(orderService)
            .orderProduct(productId);

        mockMvc.perform(post("/products/{productId}/orders", productId)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andDo(document("products/{productId}/orders/failure-notFound-user",
                pathParameters(parameterWithName("productId").description("????????? ????????? ID")
                )));
    }

    @Test
    @DisplayName("??????????????? ???????????????. :???????????? ?????? ???????????????.")
    public void orderProductTestWhenFail2() throws Exception {
        Long productId = 1L;

        doThrow(new NotFoundException("Select not found product.")).when(orderService)
            .orderProduct(productId);

        mockMvc.perform(post("/products/{productId}/orders", productId)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andDo(document("products/{productId}/orders/failure-notFound-product",
                pathParameters(parameterWithName("productId").description("????????? ????????? ID")
                )));
    }
}