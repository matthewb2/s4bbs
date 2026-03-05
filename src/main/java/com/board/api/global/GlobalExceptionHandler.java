package com.board.api.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e) {

        ExceptionResponse response =
                new ExceptionResponse(
                        e.getErrorCode().name(),
                        e.getErrorCode().getMessage()
                );

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(response);
    }
}
