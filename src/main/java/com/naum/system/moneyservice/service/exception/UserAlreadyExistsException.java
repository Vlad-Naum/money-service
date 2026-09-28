package com.naum.system.moneyservice.service.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("User already exist");
    }
}
