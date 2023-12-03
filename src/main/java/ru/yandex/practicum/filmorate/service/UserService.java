package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
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

        // Отправил ли друг запрос на дружбу
        Friendship friendToUserFriendship = friend.getFriendships().stream()
                .filter(f -> f.getFriendId() == id)
                .findFirst()
                .orElse(null);

        // Отправил ли пользователь запрос на дружбу
        Friendship userToFriendFriendship = user.getFriendships().stream()
                .filter(f -> f.getFriendId() == friendId)
                .findFirst()
                .orElse(null);

        if (friendToUserFriendship != null && friendToUserFriendship.getStatus() == FriendshipStatus.UNCONFIRMED) {
            friendToUserFriendship.setStatus(FriendshipStatus.CONFIRMED);
            user.getFriendships().add(friendToUserFriendship);
            return true;
        } else if (userToFriendFriendship == null) {
            Friendship newFriendship = new Friendship(id, friendId, FriendshipStatus.UNCONFIRMED);
            user.getFriendships().add(newFriendship);
            return true;
        }
        return false;
    }

    public boolean deleteFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        if (user == null || friend == null) {
            log.warn("Attempt to delete a friend that does not exist: User ID {}, Friend ID {}", id, friendId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Friend not found");
        }

        boolean isFriendDeletedFromUser = user.getFriendships().removeIf(f -> f.getFriendId() == friendId);
        boolean isFriendDeletedFromFriend = friend.getFriendships().removeIf(f -> f.getFriendId() == id);

        return isFriendDeletedFromUser && isFriendDeletedFromFriend;
    }

    public List<User> findFriends(int id) {
        User user = userStorage.findById(id);
        if (user == null) {
            log.warn("Attempt to contact a user that does not exist: User ID {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return user.getFriendships().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(Friendship::getFriendId)
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

        Set<Integer> userFriends = user.getFriendships().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        Set<Integer> otherUserFriends = otherUser.getFriendships().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        userFriends.retainAll(otherUserFriends);

        return userFriends.stream()
                .map(userStorage::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
