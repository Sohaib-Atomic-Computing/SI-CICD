package com.iconnect.backend.exception.handlers;

import com.iconnect.backend.dtos.Response;
import com.iconnect.backend.exception.BadRequestException;
import com.iconnect.backend.exception.ForbiddenRequestException;
import com.iconnect.backend.exception.RecordNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class RestExceptionHandler  {

    //other exception handlers

    @ExceptionHandler(RecordNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            RecordNotFoundException ex) {
        Response errorError = new Response(ex.getMessage(), false, null);
        return new ResponseEntity<>(errorError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenRequestException.class)
    protected ResponseEntity<Object> handleForbidden(
            ForbiddenRequestException ex) {
        Response errorError = new Response(ex.getMessage(), false, null);
        return new ResponseEntity<>(errorError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequest(
            BadRequestException ex) {
        Response errorError = new Response(ex.getMessage(), false, null);
        return new ResponseEntity<>(errorError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> databaseConnectionFailsException(Exception ex) {
        Response errorError = new Response(ex.getMessage(), false, null);
        return new ResponseEntity<>(errorError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

