package com.ncba.countryintegration.exception;

public class SoapIntegrationException extends RuntimeException {

    public SoapIntegrationException(String message) {
        super(message);
    }

    public SoapIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}