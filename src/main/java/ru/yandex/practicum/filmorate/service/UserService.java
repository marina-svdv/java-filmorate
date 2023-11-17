package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        if (userStorage.findById(user.getId()) != null) {
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

    public Collection<User> findAllUsers() {
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
        if (userStorage.findById(friendId) == null || userStorage.findById(id) == null) {
            log.warn("Attempt to add a friend that does not exist: User ID {}, Friend ID {}", id, friendId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Friend not found");
        }
        boolean isFriendAdded1 = userStorage.findById(id).getFriends().add(friendId);
        boolean isFriendAdded2 = userStorage.findById(friendId).getFriends().add(id);
        return (isFriendAdded1 && isFriendAdded2);
    }

    public boolean deleteFriend(int id, int friendId) {
        if (userStorage.findById(friendId) == null || userStorage.findById(id) == null) {
            log.warn("Attempt to delete a friend that does not exist: User ID {}, Friend ID {}", id, friendId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Friend not found");
        }
        boolean isFriendDeleted1 = userStorage.findById(id).getFriends().remove(friendId);
        boolean isFriendDeleted2 = userStorage.findById(friendId).getFriends().remove(id);
        return (isFriendDeleted1 && isFriendDeleted2);
    }

    public List<User> findFriends(int id) {
        if (userStorage.findById(id) == null) {
            log.warn("Attempt to contact a user that does not exist: User ID {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userStorage.findById(id).getFriends().stream()
                .map(userStorage::findById)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toList());
    }

    public Set<User> findMutualFriends(int id, int otherId) {
        if (userStorage.findById(otherId) == null || userStorage.findById(id) == null) {
            log.warn("Attempt to to contact a user that does not exist: User ID {}, OtherUser ID {}", id, otherId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or OtherUser not found");
        }
        Set<User> friends = new HashSet<>();
        Set<Integer> mutualFriends = new HashSet<>(userStorage.findById(id).getFriends());
        mutualFriends.retainAll(userStorage.findById(otherId).getFriends());
        for (int friendsId : mutualFriends) {
            friends.add(userStorage.findById(friendsId));
        }
        return friends;
    }
}
