package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    User findById(int id);

    Collection<User> findAll();

    User update(int id, User newUser);

    boolean delete(int id);
}
