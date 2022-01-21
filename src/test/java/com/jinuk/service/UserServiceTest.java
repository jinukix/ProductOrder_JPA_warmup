package com.jinuk.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jinuk.domain.User;
import com.jinuk.dto.LoginRequestDto;
import com.jinuk.dto.UserRequestDto;
import com.jinuk.exception.BadRequestException;
import com.jinuk.exception.DuplicatedException;
import com.jinuk.exception.NotFoundException;
import com.jinuk.repository.UserRepository;
import com.jinuk.utils.PasswordEncrypt;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private void injectSessionInUserService() {
        try {
            Field sessionField = userService.getClass().getDeclaredField("session");
            sessionField.setAccessible(true);
            sessionField.set(userService, new MockHttpSession());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("회원가입에 성공합니다.")
    public void signUpTestWhenSuccess() {
        UserRequestDto requestDto = UserRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        userService.signUp(requestDto);

        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("회원가입에 실패합니다. :중복된 이메일")
    public void signUpTestWhenFail() {
        UserRequestDto requestDto = UserRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);
        assertThrows(DuplicatedException.class, () -> userService.signUp(requestDto));
    }

    @Test
    @DisplayName("로그인에 성공합니다.")
    public void loginTestWhenSuccess() {
        injectSessionInUserService();
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        User user = User.builder()
            .id(1L)
            .email("email@email.com")
            .password(PasswordEncrypt.encrypt("password"))
            .build();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        userService.login(requestDto);

        verify(userRepository).findByEmail(requestDto.getEmail());
    }

    @Test
    @DisplayName("로그인에 실패합니다. :존재하지 않는 이메일")
    public void loginTestWhenFail1() {
        injectSessionInUserService();
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.login(requestDto));
    }

    @Test
    @DisplayName("로그인에 실패합니다. :잘못된 패스워드")
    public void loginTestWhenFail2() {
        injectSessionInUserService();
        LoginRequestDto requestDto = LoginRequestDto.builder()
            .email("email@email.com")
            .password("password")
            .build();

        User user = User.builder()
            .id(1L)
            .email("email@email.com")
            .password(PasswordEncrypt.encrypt("password"))
            .build();

        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));

        requestDto.setPassword("");
        assertThrows(BadRequestException.class, () -> userService.login(requestDto));

        requestDto.setPassword("password" + "@");
        assertThrows(BadRequestException.class, () -> userService.login(requestDto));

        requestDto.setPassword("password".substring(1));
        assertThrows(BadRequestException.class, () -> userService.login(requestDto));

        requestDto.setPassword(PasswordEncrypt.encrypt("password"));
        assertThrows(BadRequestException.class, () -> userService.login(requestDto));
    }

    @Test
    @DisplayName("로그아웃 성공.")
    public void logoutTestWhenSuccess() {
        injectSessionInUserService();
        userService.logout();
        assertNull(userService.getCurrentUser());
    }
}