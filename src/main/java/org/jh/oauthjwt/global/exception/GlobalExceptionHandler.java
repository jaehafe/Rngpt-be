package org.jh.oauthjwt.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(final BadRequestException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyVerifiedException(final UserAlreadyVerifiedException e) {
        log.warn(e.getMessage(), e);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}
