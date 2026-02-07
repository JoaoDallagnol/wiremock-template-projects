package com.junit.wiremock.dto;

import lombok.Data;

@Data
public class EmailValidationResponse {
    private String email;
    private boolean valid;
    private String reason;
}
