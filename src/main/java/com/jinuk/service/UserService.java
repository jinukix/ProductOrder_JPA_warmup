package com.jinuk.service;

import com.jinuk.domain.User;
import com.jinuk.dto.LoginRequestDto;
import com.jinuk.dto.UserRequestDto;
import com.jinuk.exception.BadRequestException;
import com.jinuk.exception.DuplicatedException;
import com.jinuk.exception.NotFoundException;
import com.jinuk.repository.UserRepository;
import com.jinuk.utils.PasswordEncrypt;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_ID = "USER_ID";

    private final UserRepository userRepository;
    private final HttpSession session;

    public void signUp(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new DuplicatedException("This email already exists.");
        }

        userRequestDto.setPassword(PasswordEncrypt.encrypt(userRequestDto.getPassword()));
        userRepository.save(userRequestDto.toEntity());
    }

    public void login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
            .orElseThrow(() -> new NotFoundException("Select not found user."));

        if (PasswordEncrypt.isMatch(loginRequestDto.getPassword(), user.getPassword())) {
            session.setAttribute(USER_ID, user.getId());
        } else {
            throw new BadRequestException("Your password is invalid.");
        }
    }

    public void logout() {
        session.removeAttribute(USER_ID);
    }

    public Long getCurrentUser() {
        return (Long) session.getAttribute(USER_ID);
    }
}
