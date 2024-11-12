package com.example.wallet_api.exceptions.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Insufficient funds in the wallet");
    }
}
