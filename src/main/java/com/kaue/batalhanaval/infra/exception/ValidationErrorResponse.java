package com.kaue.batalhanaval.infra.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        String message,
        List<FieldError> errors,
        LocalDateTime timestamp
) {}
