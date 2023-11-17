package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    public void testCreateValidUser() throws Exception {
        String validUser = "{\n"
                + "  \"login\": \"testLogin\",\n"
                + "  \"name\": \"Test User\",\n"
                + "  \"email\": \"test@email.com\",\n"
                + "  \"birthday\": \"1977-07-07\"\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testCreateInvalidUser() throws Exception {
        String invalidUser = "{\n"
                + "  \"login\": \"\",\n"
                + "  \"name\": \"\",\n"
                + "  \"email\": \"\",\n"
                + "  \"birthday\": \"\"\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidUser))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testCreateUserWithExistingId() throws Exception {
        String validUser = "{\n"
                + "  \"login\": \"testLogin\",\n"
                + "  \"name\": \"Test User\",\n"
                + "  \"id\": 1,\n"
                + "  \"email\": \"test@email.com\",\n"
                + "  \"birthday\": \"1977-07-07\"\n"
                + "}";
        HttpRequest initialRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();
        httpClient.send(initialRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(409, response.statusCode());
    }

    @Test
    public void testCreateUserWithEmptyBody() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        String userWithInvalidEmail = "{\n"
                + "  \"login\": \"testLogin\",\n"
                + "  \"name\": \"Test User\",\n"
                + "  \"id\": 2,\n"
                + "  \"email\": \"invalidEmail\",\n"
                + "  \"birthday\": \"1977-07-07\"\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userWithInvalidEmail))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testUpdateUser() throws Exception {
        String validUser = "{\n"
                + "  \"login\": \"testLogin\",\n"
                + "  \"name\": \"Test User\",\n"
                + "  \"id\": 1,\n"
                + "  \"email\": \"test@email.com\",\n"
                + "  \"birthday\": \"1977-07-07\"\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String updatedUser = "{\n"
                + "  \"login\": \"updatedLogin\",\n"
                + "  \"name\": \"Updated User\",\n"
                + "  \"id\": 1,\n"
                + "  \"email\": \"update@test.com\",\n"
                + "  \"birthday\": \"1980-01-01\"\n"
                + "}";
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedUser))
                .build();

        HttpResponse<String> response = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testUpdateNonExistingUser() throws Exception {
        String nonExistingUserUpdate = "{\n"
                + "  \"login\": \"ghostUser\",\n"
                + "  \"name\": \"Ghost User\",\n"
                + "  \"id\": 999,\n"
                + "  \"email\": \"ghost@test.com\",\n"
                + "  \"birthday\": \"1990-01-01\"\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(nonExistingUserUpdate))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        String validUser = "{\n"
                + "  \"login\": \"testLogin\",\n"
                + "  \"name\": \"Test User\",\n"
                + "  \"id\": 1,\n"
                + "  \"email\": \"test@email.com\",\n"
                + "  \"birthday\": \"1977-07-07\"\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/users"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
