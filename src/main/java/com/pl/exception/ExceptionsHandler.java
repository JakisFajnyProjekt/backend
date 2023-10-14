package com.pl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoudException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoudException userNotFoudException){
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(userNotFoudException.getMessage(),
                HttpStatus.NOT_FOUND.toString(),
                LocalDate.now());
        return new ResponseEntity<>(apiErrorResponse,HttpStatus.NOT_FOUND);
    }


}
