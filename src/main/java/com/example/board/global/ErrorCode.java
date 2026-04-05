package com.example.board.global;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 등록된 리소스입니다"),
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "잘못된 입력값이 있습니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "요청하신 작업 처리에 실패했습니다. 잠시 후 다시 이용해 주시기 바랍니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
