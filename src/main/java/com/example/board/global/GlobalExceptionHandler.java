package com.example.board.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", 0);
        response.put("message", "잘못된 입력값이 있습니다.");

        Map<String, Object> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("type", "field");
            errorInfo.put("value", error.getRejectedValue());
            errorInfo.put("msg", error.getDefaultMessage());
            errorInfo.put("location", "body");
            errors.put(error.getField(), errorInfo);
        });
        response.put("errors", errors);

        return ResponseEntity.status(422).body(response);
    }
}
