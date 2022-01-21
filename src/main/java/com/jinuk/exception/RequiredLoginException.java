package com.jinuk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RequiredLoginException extends RuntimeException {

    public RequiredLoginException(String msg) {
        super(msg);
    }
}