package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/filmorate/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final ValidationService validationService = new ValidationService();

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = new ArrayList<User>(validationService.getUsers().values());
        if (allUsers.isEmpty()) {
            return new ResponseEntity<>(allUsers, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<User> create(@RequestBody User user) throws ValidationException {
        try {
            if (validationService.getUsers().containsKey(user.getId())) {
                log.warn("Attempt to create user with an existing ID: {}", user.getId());
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            validationService.validateUser(user);
            user.assignId();
            validationService.getUsers().put(user.getId(), user);
            log.info("User with ID {} has been created.", user.getId());
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (ValidationException e) {
            log.error("User creation validation failed: {}", e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping()
    public ResponseEntity<User> update(@RequestBody User user) throws ValidationException {
        try {
            if (validationService.getUsers().containsKey(user.getId())) {
                validationService.validateUser(user);
                validationService.getUsers().put(user.getId(), user);
                log.info("User with ID {} has been updated.", user.getId());
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                log.warn("Attempt to update user with a non-existent ID: {}", user.getId());
                return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
            }
        } catch (ValidationException e) {
            log.error("User update validation failed: {}", e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
    }
}
