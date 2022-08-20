package com.iconnect.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends RuntimeException {

  public RecordNotFoundException() {
    super();
  }

  public RecordNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public RecordNotFoundException(String message) {
    super(message);
  }

  public RecordNotFoundException(Throwable cause) {
    super(cause);
  }

    public RecordNotFoundException(String message, Long id) {
      super(message);
    }
}
