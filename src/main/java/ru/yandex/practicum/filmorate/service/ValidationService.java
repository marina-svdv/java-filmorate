package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.ValidationNameException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Service
public class ValidationService {

    public void validateUser(User user) throws ValidationException, ValidationNameException {
        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("The email is entered incorrectly.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("The login is entered incorrectly.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("The date of birth is entered incorrectly.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationNameException();
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
