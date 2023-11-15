package ru.yandex.practicum.filmorate.exception;

public class ValidationNameException extends Exception {
    public ValidationNameException() {
        super("The user does not have a valid name.");
    }
}
