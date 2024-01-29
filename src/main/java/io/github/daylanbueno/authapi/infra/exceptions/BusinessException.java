package io.github.daylanbueno.authapi.infra.exceptions;

public class BusinessException extends  RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
