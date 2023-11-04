package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmRepository filmRepository;
    private final ValidationService validationService;

    @Autowired
    public FilmController(FilmRepository filmRepository, ValidationService validationService) {
        this.filmRepository = filmRepository;
        this.validationService = validationService;
    }

    @GetMapping()
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> allFilms = new ArrayList<>(filmRepository.findAll());
        if (allFilms.isEmpty()) {
            return new ResponseEntity<>(allFilms, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(allFilms, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Film> create(@RequestBody Film film) throws ValidationException {
        try {
            if (filmRepository.findById(film.getId()) != null) {
                log.warn("Attempt to create a film with an existing ID: {}", film.getId());
                return new ResponseEntity<>(film, HttpStatus.CONFLICT);
            }
            validationService.validateFilm(film);
            filmRepository.create(film);
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
            if (filmRepository.findById(film.getId()) == null) {
                log.warn("Attempt to update a film with a non-existent ID: {}", film.getId());
                return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
            }
            validationService.validateFilm(film);
            filmRepository.update(film.getId(), film);
            log.info("Film with ID {} has been updated.", film.getId());
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (ValidationException e) {
            log.error("Film update validation failed: {}", e.getMessage());
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }
}
