package com.junit.wiremock.service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.junit.wiremock.client.EmailValidationClient;
import com.junit.wiremock.entity.User;
import com.junit.wiremock.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceWithWireMockTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_WhenEmailIsValid_ShouldCreateUser() {
        EmailValidationClient emailClient = new EmailValidationClient(new RestTemplate());
        ReflectionTestUtils.setField(emailClient, "apiUrl", wireMock.baseUrl());
        ReflectionTestUtils.setField(userService, "emailValidationClient", emailClient);

        wireMock.stubFor(get(urlPathEqualTo("/validate"))
                .withQueryParam("email", equalTo("john@example.com"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"email\":\"john@example.com\",\"valid\":true,\"reason\":\"Valid email\"}")));

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");

        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).save(user);
        wireMock.verify(getRequestedFor(urlPathEqualTo("/validate"))
                .withQueryParam("email", equalTo("john@example.com")));
    }

    @Test
    void createUser_WhenEmailIsInvalid_ShouldThrowException() {
        EmailValidationClient emailClient = new EmailValidationClient(new RestTemplate());
        ReflectionTestUtils.setField(emailClient, "apiUrl", wireMock.baseUrl());
        ReflectionTestUtils.setField(userService, "emailValidationClient", emailClient);

        wireMock.stubFor(get(urlPathEqualTo("/validate"))
                .withQueryParam("email", equalTo("invalid@fake.com"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"email\":\"invalid@fake.com\",\"valid\":false,\"reason\":\"Domain does not exist\"}")));

        User user = new User();
        user.setName("John Doe");
        user.setEmail("invalid@fake.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(user));

        assertTrue(exception.getMessage().contains("Invalid email"));
        verify(userRepository, never()).save(any());
        wireMock.verify(getRequestedFor(urlPathEqualTo("/validate"))
                .withQueryParam("email", equalTo("invalid@fake.com")));
    }

    @Test
    void createUser_WhenApiReturns404_ShouldThrowException() {
        EmailValidationClient emailClient = new EmailValidationClient(new RestTemplate());
        ReflectionTestUtils.setField(emailClient, "apiUrl", wireMock.baseUrl());
        ReflectionTestUtils.setField(userService, "emailValidationClient", emailClient);

        wireMock.stubFor(get(urlPathEqualTo("/validate"))
                .willReturn(aResponse().withStatus(404)));

        User user = new User();
        user.setName("John Doe");
        user.setEmail("test@example.com");

        assertThrows(Exception.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_WhenApiTimeout_ShouldThrowException() {
        EmailValidationClient emailClient = new EmailValidationClient(new RestTemplate());
        ReflectionTestUtils.setField(emailClient, "apiUrl", wireMock.baseUrl());
        ReflectionTestUtils.setField(userService, "emailValidationClient", emailClient);

        wireMock.stubFor(get(urlPathEqualTo("/validate"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(5000)));

        User user = new User();
        user.setName("John Doe");
        user.setEmail("test@example.com");

        assertThrows(Exception.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }
}
