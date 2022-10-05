package io.satra.iconnect.vendor_service.exception;

public class InvalidNameException extends Exception {
    public InvalidNameException(String name) {
        super("Invalid validator name: %s".formatted(name));
    }
}
