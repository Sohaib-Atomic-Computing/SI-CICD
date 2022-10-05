package io.satra.iconnect.vendor_service.controller;

import io.satra.iconnect.vendor_service.exception.InvalidNameException;
import io.satra.iconnect.vendor_service.exception.NoVendorForValidatorFoundException;
import io.satra.iconnect.vendor_service.exception.ValidatorNotFoundException;
import io.satra.iconnect.vendor_service.exception.VendorNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    public ResponseEntity<Exception> handleGenericException(Exception exception) {
        log.error("Generic Exception occurred!", exception);
        return ResponseEntity.internalServerError().body(exception);
    }

    @ExceptionHandler(value = {InvalidNameException.class})
    public ResponseEntity<Void> handleNotFoundException(InvalidNameException exception) {
        log.error("Invalid validator name!", exception);
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = {NoVendorForValidatorFoundException.class, ValidatorNotFoundException.class, VendorNotFoundException.class})
    public ResponseEntity<Void> handleNotFoundException(RuntimeException exception) {
        log.error("Ressource not found!", exception);
        return ResponseEntity.notFound().build();
    }
}
