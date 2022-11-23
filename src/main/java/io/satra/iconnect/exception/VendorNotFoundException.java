package io.satra.iconnect.exception;

public class VendorNotFoundException extends Exception {
    public VendorNotFoundException(String id) {
        super("Vendor with id %s not found!".formatted(id));
    }
}