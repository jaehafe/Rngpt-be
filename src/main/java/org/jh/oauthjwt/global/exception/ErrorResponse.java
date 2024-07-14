package org.jh.oauthjwt.global.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int code;
    private final String message;

    ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
