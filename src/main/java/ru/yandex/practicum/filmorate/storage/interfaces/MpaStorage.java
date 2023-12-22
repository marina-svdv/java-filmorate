package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    Mpa create(Mpa mpa);

    Mpa findById(int id);

    List<Mpa> findAll();

    Mpa update(int id, Mpa newMpa);

    boolean delete(int id);
}
