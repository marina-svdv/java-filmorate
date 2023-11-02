package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
public class UserControllerTest {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String URL = "http://localhost:8080/filmorate/users";

    @Test
    public void testCreateValidUser() throws Exception {
        String validUser = """
            {
                "login": "testLogin",
                "name": "Test User",
                "email": "test@email.com",
                "birthday": "1977-07-07"
            }
            """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testCreateInvalidUser() throws Exception {
        String invalidUser = """
                {
                        
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidUser))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testCreateUserWithExistingId() throws Exception {
        String validUser = """
            {
                "login": "testLogin",
                "name": "Test User",
                "id": 1,
                "email": "test@email.com",
                "birthday": "1977-07-07"
            }
            """;
        HttpRequest initialRequest = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();
        httpClient.send(initialRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(409, response.statusCode());
    }

    @Test
    public void testCreateUserWithEmptyBody() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        String userWithInvalidEmail = """
                {
                    "login": "testLogin",
                    "name": "Test User",
                    "id": 2,
                    "email": "invalidEmail",
                    "birthday": "1977-07-07"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(userWithInvalidEmail))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testUpdateUser() throws Exception {
        String validUser = """
            {
                "login": "testLogin",
                "name": "Test User",
                "id": 1,
                "email": "test@email.com",
                "birthday": "1977-07-07"
            }
            """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String updatedUser = """
                {
                    "login": "updatedLogin",
                    "name": "Updated User",
                    "id": 1,
                    "email": "update@test.com",
                    "birthday": "1980-01-01"
                }
                """;
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedUser))
                .build();

        HttpResponse<String> response = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testUpdateNonExistingUser() throws Exception {
        String nonExistingUserUpdate = """
                {
                    "login": "ghostUser",
                    "name": "Ghost User",
                    "id": 999,
                    "email": "ghost@test.com",
                    "birthday": "1990-01-01"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL + "/999"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(nonExistingUserUpdate))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
    @Test
    public void testGetAllUsers() throws Exception {
        String validUser = """
            {
                "login": "testLogin",
                "name": "Test User",
                "id": 1,
                "email": "test@email.com",
                "birthday": "1977-07-07"
            }
            """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validUser))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
