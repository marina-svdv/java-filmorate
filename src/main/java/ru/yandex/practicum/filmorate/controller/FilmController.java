package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping()
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmService.findAllFilms());
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.findFilmById(id);
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable int id, @PathVariable int userId) {
        User user = userService.findUserById(userId);
        Film film = filmService.findFilmById(id);
        return filmService.addLike(film, user);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean removeLike(@PathVariable int id, @PathVariable int userId) {
        User user = userService.findUserById(userId);
        Film film = filmService.findFilmById(id);
        return filmService.deleteLike(film, user);

    }

    //"/popular?count={count}"
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        return filmService.findPopularFilms(count);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFilm(@PathVariable int id) {
        return filmService.deleteFilm(id);
    }
}
