package io.satra.iconnect.exception;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException(String mobile) {
        super(String.format("User with mobile number %s already exists", mobile));
    }
}
