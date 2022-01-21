package com.jinuk.aop;

import com.jinuk.exception.RequiredLoginException;
import com.jinuk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginCheckAspect {

    private final UserService loginService;

    @Before("@annotation(com.jinuk.annotation.LoginCheck)")
    public void loginCheck() {
        if (loginService.getCurrentUser() == null) {
            throw new RequiredLoginException("This service requires login");
        }
    }
}
