package io.satra.iconnect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingRefreshTokenException extends Exception {

    public MissingRefreshTokenException() {
        super("Refresh Token is missing!");
    }
}
