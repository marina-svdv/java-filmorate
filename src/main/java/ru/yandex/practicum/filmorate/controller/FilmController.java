package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final ValidationService validationService = new ValidationService();

    @GetMapping()
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> allFilms = new ArrayList<Film>(validationService.getFilms().values());
        if (allFilms.isEmpty()) {
            return new ResponseEntity<>(allFilms, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(allFilms, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Film> create(@RequestBody Film film) throws ValidationException {
        try {
            if (validationService.getFilms().containsKey(film.getId())) {
                log.warn("Attempt to create a film with an existing ID: {}", film.getId());
                return new ResponseEntity<>(film, HttpStatus.CONFLICT);
            }
            validationService.validateFilm(film);
            film.assignId();
            validationService.getFilms().put(film.getId(), film);
            log.info("Film with ID {} has been created.", film.getId());
            return new ResponseEntity<>(film, HttpStatus.CREATED);
        } catch (ValidationException e) {
            log.error("Film creation validation failed: {}", e.getMessage());
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping()
    public ResponseEntity<Film> update(@RequestBody Film film) throws ValidationException {
        try {
            if (validationService.getFilms().containsKey(film.getId())) {
                validationService.validateFilm(film);
                validationService.getFilms().put(film.getId(), film);
                log.info("Film with ID {} has been updated.", film.getId());
                return new ResponseEntity<>(film, HttpStatus.OK);
            } else {
                log.warn("Attempt to update a film with a non-existent ID: {}", film.getId());
                return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
            }
        } catch (ValidationException e) {
            log.error("Film update validation failed: {}", e.getMessage());
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }
}
