package com.bookstore.exception;

public class OrderProcessingException extends IllegalStateException {
    public OrderProcessingException(String message) {
        super(message);
    }
}
