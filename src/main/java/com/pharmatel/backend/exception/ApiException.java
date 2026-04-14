package com.pharmatel.backend.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
