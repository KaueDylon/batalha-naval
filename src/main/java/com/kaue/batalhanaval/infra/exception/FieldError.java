package com.kaue.batalhanaval.infra.exception;

public record FieldError(
        String field,
        String message
) {}
