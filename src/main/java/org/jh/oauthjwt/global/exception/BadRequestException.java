package org.jh.oauthjwt.global.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final int code;
    private final String message;

    public BadRequestException(final ErrorResponse errorResponse) {
        this.code = errorResponse.getCode();
        this.message = errorResponse.getMessage();
    }
}
