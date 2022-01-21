package com.jinuk.controller;

import com.jinuk.annotation.LoginCheck;
import com.jinuk.dto.LoginRequestDto;
import com.jinuk.dto.UserRequestDto;
import com.jinuk.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> signUp(
        @Valid @RequestBody UserRequestDto userRequestDto) {
        userService.signUp(userRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        userService.login(loginRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @LoginCheck
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}