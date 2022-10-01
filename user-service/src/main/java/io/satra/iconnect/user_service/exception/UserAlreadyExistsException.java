package io.satra.iconnect.user_service.exception;

public class UserAlreadyExistsException extends Exception {

  public UserAlreadyExistsException(String mobile) {
    super("User with mobile %s already exists!".formatted(mobile));
  }
}
