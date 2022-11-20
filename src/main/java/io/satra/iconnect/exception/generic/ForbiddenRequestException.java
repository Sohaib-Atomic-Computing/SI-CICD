package io.satra.iconnect.exception.generic;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenRequestException extends RuntimeException {

    public ForbiddenRequestException(String message) {
        super(message);
    }

    public ForbiddenRequestException(Throwable cause) {
        super(cause);
    }

    public ForbiddenRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
