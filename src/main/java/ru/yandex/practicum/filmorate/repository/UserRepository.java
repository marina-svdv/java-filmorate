package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Repository
public class UserRepository {
    private static int COUNTER = 1;

    private final HashMap<Integer, User> users = new HashMap<>();

    public User create(User user) {
        user.setId(COUNTER++);
        users.put(user.getId(), user);
        log.info("User created: {}", user);
        return user;
    }

    public User findById(int id) {
        log.info("Searching for user with ID: {}", id);
        return users.get(id);
    }

    public Collection<User> findAll() {
        log.info("Retrieving all users");
        return users.values();
    }

    public User update(int id, User newUser) {
        if (users.containsKey(id)) {
            newUser.setId(id);
            users.put(id, newUser);
            log.info("User updated: {}", newUser);
        } else {
            log.warn("Attempted to update non-existing user with ID: {}", id);
        }
        return newUser;
    }

    public boolean delete(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("User deleted with ID: {}", id);
            return true;
        }
        log.warn("Attempted to delete non-existing user with ID: {}", id);
        return false;
    }
}
