package com.twitter.api.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorDetails extends ErrorDetails {
    private final Map<String, String> errors;

    public ValidationErrorDetails(LocalDateTime timestamp, String message, String details, Map<String, String> errors) {
        super(timestamp, message, details);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
