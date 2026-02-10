package com.junit.wiremock.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("email.validation.api.url", wireMock::baseUrl);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void createUser_WhenEmailIsValid_ShouldReturn201() {
        wireMock.stubFor(get(urlPathEqualTo("/validate"))
                .withQueryParam("email", com.github.tomakehurst.wiremock.client.WireMock.equalTo("john@example.com"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"email\":\"john@example.com\",\"valid\":true,\"reason\":\"Valid email\"}")));

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}")
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("john@example.com"))
                .body("id", notNullValue());

        wireMock.verify(getRequestedFor(urlPathEqualTo("/validate"))
                .withQueryParam("email", com.github.tomakehurst.wiremock.client.WireMock.equalTo("john@example.com")));
    }

    @Test
    void createUser_WhenEmailIsInvalid_ShouldReturn500() {
        wireMock.stubFor(get(urlPathEqualTo("/validate"))
                .withQueryParam("email", com.github.tomakehurst.wiremock.client.WireMock.equalTo("invalid@fake.com"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"email\":\"invalid@fake.com\",\"valid\":false,\"reason\":\"Domain does not exist\"}")));

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"John Doe\",\"email\":\"invalid@fake.com\"}")
                .when()
                .post("/api/users")
                .then()
                .statusCode(500);

        wireMock.verify(getRequestedFor(urlPathEqualTo("/validate"))
                .withQueryParam("email", com.github.tomakehurst.wiremock.client.WireMock.equalTo("invalid@fake.com")));
    }

    @Test
    void createUser_WhenValidationApiIsDown_ShouldReturn500() {
        wireMock.stubFor(get(urlPathEqualTo("/validate"))
                .willReturn(aResponse().withStatus(500)));

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"John Doe\",\"email\":\"test@example.com\"}")
                .when()
                .post("/api/users")
                .then()
                .statusCode(500);
    }
}