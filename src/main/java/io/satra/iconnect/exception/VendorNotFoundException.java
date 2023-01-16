package io.satra.iconnect.exception;

public class VendorNotFoundException extends Exception {
    public VendorNotFoundException(String id) {
        super(String.format("Vendor with id %s not found", id));
    }
}