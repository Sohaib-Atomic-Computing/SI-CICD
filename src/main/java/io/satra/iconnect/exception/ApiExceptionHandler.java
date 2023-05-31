package io.satra.iconnect.exception;

import io.satra.iconnect.dto.response.ResponseDTO;
import io.satra.iconnect.exception.generic.BadRequestException;
import io.satra.iconnect.exception.generic.EntityNotFoundException;
import io.satra.iconnect.exception.generic.ForbiddenRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<Object> handleApiException(BadRequestException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .timestamp(ZonedDateTime.now())
                        .message(ex.getMessage())
                        .success(false)
                        .build()
        );
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<Object> handleApiException(EntityNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseDTO.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .timestamp(ZonedDateTime.now())
                        .message(ex.getMessage())
                        .success(false)
                        .build()
        );
    }

    @ExceptionHandler(value = ForbiddenRequestException.class)
    public ResponseEntity<Object> handleApiException(ForbiddenRequestException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDTO.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .timestamp(ZonedDateTime.now())
                        .message(ex.getMessage())
                        .success(false)
                        .build()
        );
    }

    @ExceptionHandler(value = MissingRefreshTokenException.class)
    public ResponseEntity<Object> handleApiException(MissingRefreshTokenException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .timestamp(ZonedDateTime.now())
                        .message(ex.getMessage())
                        .success(false)
                        .build()
        );
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> handleApiException(IllegalArgumentException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .timestamp(ZonedDateTime.now())
                        .message(ex.getMessage())
                        .success(false)
                        .build()
        );
    }

    // bad credentials
    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<Object> handleApiException(BadCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseDTO.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .timestamp(ZonedDateTime.now())
                        .message(ex.getMessage())
                        .success(false)
                        .build()
        );
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleApiException(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseDTO.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .timestamp(ZonedDateTime.now())
                        .message(ex.getMessage())
                        .success(false)
                        .build()
        );
    }
}
