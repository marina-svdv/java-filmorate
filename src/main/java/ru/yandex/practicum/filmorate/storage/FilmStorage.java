package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film findById(int id);

    List<Film> findAll();

    Film update(int id, Film newFilm);

    boolean delete(int id);
}
