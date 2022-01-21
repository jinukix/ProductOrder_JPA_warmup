package com.jinuk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinuk.dto.LoginRequestDto;
import com.jinuk.dto.UserRequestDto;
import com.jinuk.exception.BadRequestException;
import com.jinuk.exception.DuplicatedException;
import com.jinuk.exception.NotFoundException;
import com.jinuk.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(sharedHttpSession())
            .build();
    }

    @Test
    @DisplayName("회원가입에 성공합니다.")
    public void signUpTestWhenSuccess() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        doNothing().when(userService).signUp(userRequestDto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequestDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andDo(document("users/signUp/successful", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("user's email"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("user's password")
            )));
    }

    @Test
    @DisplayName("회원가입에 실패합니다. :중복된 이메일입니다.")
    public void signUpTestWhenFail() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        doThrow(new DuplicatedException("This email already exists.")).when(userService)
            .signUp(any());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isConflict())
            .andDo(document("users/signUp/failure", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("user's email"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("user's password")
            )));
    }

    @Test
    @DisplayName("로그인에 성공합니다.")
    public void loginTestWhenSuccess() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        doNothing().when(userService).login(requestDto);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/login/successful", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("user's email"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("user's password")
            )));
    }

    @Test
    @DisplayName("로그인에 실패합니다. :존재하지 않는 이메일입니다.")
    public void loginTestWhenFail() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        doThrow(new NotFoundException("Select not found user.")).when(userService).login(any());

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andDo(document("users/login/failure-notFound", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("user's email"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("user's password")
            )));
    }

    @Test
    @DisplayName("로그인에 실패합니다. :비밀번호가 일치하지 않습니다.")
    public void loginTestWhenFail2() throws Exception {
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        doThrow(new BadRequestException("Your password is invalid.")).when(userService)
            .login(any());

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andDo(document("users/login/failure-badRequest", requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("user's email"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("user's password")
            )));
    }

    @Test
    @DisplayName("로그아웃에 성공합니다.")
    public void logoutTestWhenSuccess() throws Exception {
        doNothing().when(userService).logout();

        mockMvc.perform(post("/users/logout"))
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("users/logout"));
    }
}