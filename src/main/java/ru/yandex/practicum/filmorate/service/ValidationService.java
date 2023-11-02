package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.HashMap;

public class ValidationService {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final HashMap<Integer, User> users = new HashMap<>();

    public HashMap<Integer, Film> getFilms() {
        return films;
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }

    public void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("The email is entered incorrectly.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("The login is entered incorrectly.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            if (users.containsValue(user)) {
                throw new ValidationException("The user with the same login and name already exists.");
            }
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("The date of birth is entered incorrectly.");
        }
    }

    public void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("The name is empty or missing.");
        }
        if (film.getDescription().length() > 199) {
            throw new ValidationException("The description length exceeded, length no more than 200 characters.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("The release date cannot be earlier than December 28, 1895.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("The duration of the film must be more than zero minutes.");
        }
    }
}
