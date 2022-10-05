package io.satra.iconnect.vendor_service.exception;

public class NoVendorForValidatorFoundException extends Exception {
    public NoVendorForValidatorFoundException(String validatorId) {
        super("No vendor found for validator with id %s".formatted(validatorId));
    }
}
