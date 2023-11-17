package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Film findById(int id);

    Collection<Film> findAll();

    Film update(int id, Film newFilm);

    boolean delete(int id);
}
