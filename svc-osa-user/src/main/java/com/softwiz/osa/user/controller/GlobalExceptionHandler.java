package com.softwiz.osa.user.controller;

import com.softwiz.osa.user.entity.CustomErrorResponse;
import com.softwiz.osa.user.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Default exception handler for handling other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleOtherExceptions(Exception ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setMessage("An unexpected error occurred");
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
