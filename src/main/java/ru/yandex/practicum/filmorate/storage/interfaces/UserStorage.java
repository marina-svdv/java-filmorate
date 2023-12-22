package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User findById(int id);

    List<User> findAll();

    User update(int id, User newUser);

    boolean delete(int id);
}
