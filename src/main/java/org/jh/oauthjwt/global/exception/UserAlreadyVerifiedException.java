package org.jh.oauthjwt.global.exception;

import lombok.Getter;

@Getter
public class UserAlreadyVerifiedException extends RuntimeException {
    private final int code;

    public UserAlreadyVerifiedException(int code, String message) {
        super(message);
        this.code = code;
    }

}
