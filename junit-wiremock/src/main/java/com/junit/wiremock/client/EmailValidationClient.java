package com.junit.wiremock.client;

import com.junit.wiremock.dto.EmailValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EmailValidationClient {

    private final RestTemplate restTemplate;
    
    @Value("${email.validation.api.url}")
    private String apiUrl;

    public EmailValidationResponse validateEmail(String email) {
        String url = apiUrl + "/validate?email=" + email;
        return restTemplate.getForObject(url, EmailValidationResponse.class);
    }
}
