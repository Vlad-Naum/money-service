package com.naum.system.moneyservice.service.exception;

public class UserNotFoundException extends RuntimeException {

    private UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException userNotFoundByIdException(Long id) {
        return new UserNotFoundException("User id=%d not found!".formatted(id));
    }

    public static UserNotFoundException userNotFoundByEmailException(String email) {
        return new UserNotFoundException("User email=%s not found!".formatted(email));
    }
}
