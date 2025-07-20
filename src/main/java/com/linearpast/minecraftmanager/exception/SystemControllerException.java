package com.linearpast.minecraftmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SystemControllerException extends RuntimeException{
    public SystemControllerException(String message) {
        super(message);
    }
}
