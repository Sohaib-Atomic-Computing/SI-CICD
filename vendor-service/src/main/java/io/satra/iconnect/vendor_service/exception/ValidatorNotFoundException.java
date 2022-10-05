package io.satra.iconnect.vendor_service.exception;

public class ValidatorNotFoundException extends Exception {
    public ValidatorNotFoundException(String id) {
        super("Validator with id %s not found!".formatted(id));
    }
}
