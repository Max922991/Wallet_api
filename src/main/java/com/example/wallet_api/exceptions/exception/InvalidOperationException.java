package com.example.wallet_api.exceptions.exception;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException() {
        super("Invalid operation");
    }
}
