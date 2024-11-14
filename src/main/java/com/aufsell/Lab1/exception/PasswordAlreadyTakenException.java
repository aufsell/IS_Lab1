package com.aufsell.Lab1.exception;

public class PasswordAlreadyTakenException extends RuntimeException {
    public PasswordAlreadyTakenException(String message) {
        super(message);
    }
}
