package com.example.wallet_app.exceptions;

public class InsufficientWalletException extends BadRequestException {
    public InsufficientWalletException(String message) {
        super(message);
    }
    public InsufficientWalletException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
