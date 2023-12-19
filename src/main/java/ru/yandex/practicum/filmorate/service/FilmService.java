package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserService userService;

    public Film createFilm(Film film) {
        if (film.getId() != null) {
            log.warn("Attempt to create film with an existing ID: {}", film.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Film with given ID already exists");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The release date cannot be earlier than December 28, 1895.");
        }
        Film createdFilm = filmStorage.create(film);
        log.info("Film with ID {} has been created.", film.getId());
        return createdFilm;
    }

    public Film findFilmById(int id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        return film;
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    public Film updateFilm(Film newFilm) {
        if (filmStorage.findById(newFilm.getId()) == null) {
            log.warn("Attempt to update a film with a non-existent ID: {}", newFilm.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The release date cannot be earlier than December 28, 1895.");
        }
        Film updatedFilm = filmStorage.update(newFilm.getId(), newFilm);
        log.info("Film with ID {} has been updated.", newFilm.getId());
        return updatedFilm;
    }

    public boolean deleteFilm(int id) {
        if (filmStorage.findById(id) == null) {
            log.warn("Attempt to delete non-existing film with ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        return filmStorage.delete(id);
    }

    public boolean addLike(int id, int userId) {
        User user = userService.findUserById(userId);
        Film film = findFilmById(id);
        if (film == null || user == null) {
            log.warn("Attempt to contact a user or a film that does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Film not found.");
        }
        Set<Integer> likes = film.getLikes();
        if (!likes.contains(user.getId())) {
            likes.add(user.getId());
            log.info("Like added for user {} to film {}", user.getId(), film.getId());
            filmStorage.update(film.getId(), film);
        } else {
            log.info("User {} already liked film {}", user.getId(), film.getId());
        }
        return true;
    }

    public boolean deleteLike(int id, int userId) {
        User user = userService.findUserById(userId);
        Film film = findFilmById(id);
        if (film == null || user == null) {
            log.warn("Attempt to contact a user or a film that does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Film not found.");
        }
        Set<Integer> likes = film.getLikes();
        if (likes.contains(user.getId())) {
            likes.remove(user.getId());
            log.info("Like deleted for user {} to film {}", user.getId(), film.getId());
            filmStorage.update(film.getId(), film);
            return true;
        } else {
            log.info("User {} did not like the film {}", user.getId(), film.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Like was not deleted.");
        }
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
