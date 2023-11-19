package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private static int COUNTER = 1;

    private final HashMap<Integer, Film> films = new HashMap<>();

    public Film create(Film film) {
        film.setId(COUNTER++);
        films.put(film.getId(), film);
        log.info("Film created: {}", film);
        return film;
    }

    public Film findById(int id) {
        log.info("Searching for film with ID: {}", id);
        return films.get(id);
    }

    public List<Film> findAll() {
        log.info("Retrieving all films");
        return new ArrayList<>(films.values());
    }

    public Film update(int id, Film newFilm) {
        if (films.containsKey(id)) {
            newFilm.setId(id);
            films.put(id, newFilm);
            log.info("Film updated: {}", newFilm);
        } else {
            log.warn("Attempted to update non-existing film with ID: {}", id);
        }
        return newFilm;
    }

    public boolean delete(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Film deleted with ID: {}", id);
            return true;
        }
        log.warn("Attempted to delete non-existing film with ID: {}", id);
        return false;
    }
}
