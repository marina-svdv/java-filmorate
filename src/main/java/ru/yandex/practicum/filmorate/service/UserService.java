package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        if (user.getId() != null) {
            log.warn("Attempt to create user with an existing ID: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with given ID already exists");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("User with ID {} has been created with login as name.", user.getId());
            return userStorage.create(user);
        }
        User createdUser = userStorage.create(user);
        log.info("User with ID {} has been created.", user.getId());
        return createdUser;
    }

    public User findUserById(int id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    public List<User> findAllUsers() {
        return userStorage.findAll();
    }

    public User updateUser(User newUser) {
        User updatedUser;
        if (userStorage.findById(newUser.getId()) == null) {
            log.warn("Attempt to update non-existing user with ID: {}", newUser.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            updatedUser = userStorage.update(newUser.getId(), newUser);
            log.info("User with ID {} has been updated with login as name.", newUser.getId());
            return updatedUser;
        }
        updatedUser = userStorage.update(newUser.getId(), newUser);
        log.info("User with ID {} has been updated.", newUser.getId());
        return updatedUser;
    }

    public boolean deleteUser(int id) {
        if (userStorage.findById(id) == null) {
            log.warn("Attempt to delete non-existing user with ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userStorage.delete(id);
    }

    public boolean addFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        if (user == null || friend == null) {
            log.warn("Attempt to add a friend that does not exist: User ID {}, Friend ID {}", id, friendId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Friend not found");
        }
        user.getFriends().add(friendId);
        return userStorage.update(id, user) != null;
    }

    public boolean deleteFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        if (user == null || friend == null) {
            log.warn("Attempt to delete a friend that does not exist: User ID {}, Friend ID {}", id, friendId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Friend not found");
        }
        user.getFriends().remove(friendId);
        return userStorage.update(id, user) != null;
    }

    public List<User> findFriends(int id) {
        User user = userStorage.findById(id);
        if (user == null) {
            log.warn("Attempt to contact a user that does not exist: User ID {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return user.getFriends().stream()
                .map(userStorage::findById)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());
    }

    public Set<User> findMutualFriends(int id, int otherId) {
        User user = userStorage.findById(id);
        User otherUser = userStorage.findById(otherId);

        if (user == null || otherUser == null) {
            log.warn("Attempt to contact a user that does not exist: User ID {}, OtherUser ID {}", id, otherId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or OtherUser not found");
        }

        Set<Integer> mutualFriends = new HashSet<>(user.getFriends());
        mutualFriends.retainAll(otherUser.getFriends());

        return mutualFriends.stream()
                .map(userStorage::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
