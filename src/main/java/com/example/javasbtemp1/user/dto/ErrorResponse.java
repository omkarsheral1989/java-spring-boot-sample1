package com.example.javasbtemp1.user.dto;

import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private Instant timestamp;
    private int status;
    private String message;
    private Map<String, String> fieldErrors;
}
