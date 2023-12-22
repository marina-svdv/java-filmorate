package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre create(Genre genre);

    Genre findById(int id);

    List<Genre> findAll();

    Genre update(int id, Genre newGenre);

    boolean delete(int id);
}
