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
public class FilmControllerTest {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    public void testCreateValidFilm() throws Exception {
        String validFilm = "{\n"
                + "  \"name\": \"Test Film\",\n"
                + "  \"description\": \"A test film description\",\n"
                + "  \"releaseDate\": \"2000-01-01\",\n"
                + "  \"duration\": 120\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testCreateInvalidFilm() throws Exception {
        String invalidFilm = "{\n"
                + "  \"name\": \"\",\n"
                + "  \"description\": \"A test film description\",\n"
                + "  \"releaseDate\": \"2000-01-01\",\n"
                + "  \"duration\": -120\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testCreateFilmWithExistingId() throws Exception {
        String validFilm = "{\n"
                + "  \"id\": 1,\n"
                + "  \"name\": \"Test Film\",\n"
                + "  \"description\": \"A test film description\",\n"
                + "  \"releaseDate\": \"2000-01-01\",\n"
                + "  \"duration\": 120\n"
                + "}";
        HttpRequest initialRequest = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validFilm))
                .build();
        httpClient.send(initialRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(409, response.statusCode());
    }

    @Test
    public void testUpdateFilm() throws Exception {
        String validFilm = "{\n"
                + "  \"name\": \"Test Film\",\n"
                + "  \"description\": \"A test film description\",\n"
                + "  \"releaseDate\": \"2000-01-01\",\n"
                + "  \"duration\": 120\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validFilm))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String updatedFilm = "{\n"
                + "  \"id\": 1,\n"
                + "  \"name\": \"New Test Film\",\n"
                + "  \"description\": \"A updated test film description\",\n"
                + "  \"releaseDate\": \"2001-01-01\",\n"
                + "  \"duration\": 110\n"
                + "}";
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testUpdateNonExistingFilm() throws Exception {
        String nonExistingFilm = "{\n"
                + "  \"id\": 999,\n"
                + "  \"name\": \"Test Film\",\n"
                + "  \"description\": \"A test film description\",\n"
                + "  \"releaseDate\": \"2000-01-01\",\n"
                + "  \"duration\": 120\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(nonExistingFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testCreateFilmWithEmptyBody() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testCreateFilmWithInvalidName() throws Exception {
        String filmWithInvalidName = "{\n"
                + "  \"name\": \"\",\n"
                + "  \"description\": \"A test film description\",\n"
                + "  \"releaseDate\": \"2000-01-01\",\n"
                + "  \"duration\": 120\n"
                + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmWithInvalidName))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetAllFilms() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/filmorate/films"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}