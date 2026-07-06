package com.example.platform.common.exception;

public class EmbeddingException extends RuntimeException {

    public EmbeddingException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmbeddingException(String message) {
        super(message);
    }
}

