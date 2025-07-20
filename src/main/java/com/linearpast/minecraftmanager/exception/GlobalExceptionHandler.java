package com.linearpast.minecraftmanager.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public String handlerUnauthorized(UnauthorizedException e){
        if(e.getMessage().startsWith("redirect:")){
            return e.getMessage();
        }
        return "error/404.html";
    }

    @ExceptionHandler(SystemControllerException.class)
    public String handlerSystemError(SystemControllerException e){
        return "error/500.html";
    }
}
