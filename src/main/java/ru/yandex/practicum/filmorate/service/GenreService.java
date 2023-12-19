package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        return new ArrayList<>(genreStorage.findAll());
    }

    public Genre getGenreById(int id) {
        Genre genre = genreStorage.findById(id);
        if (genre == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        return genre;
    }

    public Genre createGenre(Genre genre) {
        if (genreStorage.findById(genre.getId()) != null) {
            log.warn("Attempt to create Genre with an existing ID: {}", genre.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Genre with given ID already exists");
        }
        Genre  createdGenre = genreStorage.create(genre);
        log.info("Genre with ID {} has been created.", genre.getId());
        return createdGenre;
    }

    public Genre updateGenre(Genre newGenre) {
        Genre updatedGenre;
        if (genreStorage.findById(newGenre.getId()) == null) {
            log.warn("Attempt to update non-existing Genre with ID: {}", newGenre.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        updatedGenre = genreStorage.update(newGenre.getId(), newGenre);
        log.info("Genre with ID {} has been updated.", newGenre.getId());
        return updatedGenre;
    }
}
