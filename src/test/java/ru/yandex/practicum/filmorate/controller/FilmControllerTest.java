package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
public class FilmControllerTest {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String URL = "http://localhost:8080/filmorate/films";

    @Test
    public void testCreateValidFilm() throws Exception {
        String validFilm = """
                {
                    "name": "Test Film",
                    "description": "A test film description",
                    "releaseDate": "2000-01-01",
                    "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testCreateInvalidFilm() throws Exception {
        String invalidFilm = """
                {
                    "name": "",
                    "description": "A test film description",
                    "releaseDate": "2000-01-01",
                    "duration": -120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testCreateFilmWithExistingId() throws Exception {
        String validFilm = """
                {
                    "id": 1,
                    "name": "Test Film",
                    "description": "A test film description",
                    "releaseDate": "2000-01-01",
                    "duration": 120
                }
                """;
        HttpRequest initialRequest = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validFilm))
                .build();
        httpClient.send(initialRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(409, response.statusCode());
    }

    @Test
    public void testUpdateFilm() throws Exception {
        String validFilm = """
                {
                    "name": "Test Film",
                    "description": "A test film description",
                    "releaseDate": "2000-01-01",
                    "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(validFilm))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String updatedFilm = """
                {
                    "id": 1,
                    "name": "New Test Film",
                    "description": "A updated test film description",
                    "releaseDate": "2001-01-01",
                    "duration": 110
                }
                """;
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testUpdateNonExistingFilm() throws Exception {
        String nonExistingFilm = """
                {
                    "id": 999,
                    "name": "Test Film",
                    "description": "A test film description",
                    "releaseDate": "2000-01-01",
                    "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(nonExistingFilm))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testCreateFilmWithEmptyBody() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testCreateFilmWithInvalidName() throws Exception {
        String filmWithInvalidName = """
                {
                    "name": "",
                    "description": "A test film description",
                    "releaseDate": "2000-01-01",
                    "duration": 120
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(filmWithInvalidName))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetAllFilms() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}