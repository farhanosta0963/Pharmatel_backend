package com.pharmatel.backend.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ApiErrorResponse {
    LocalDateTime timestamp;
    int status;
    String error;
    String errorCode;
    String message;
    String path;
}
