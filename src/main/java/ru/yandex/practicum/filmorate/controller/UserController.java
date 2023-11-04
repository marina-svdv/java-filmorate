package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.ValidationNameException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final ValidationService validationService;

    @Autowired
    public UserController(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = new ArrayList<User>(userRepository.findAll());
        if (allUsers.isEmpty()) {
            return new ResponseEntity<>(allUsers, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<User> create(@RequestBody User user) {
        try {
            if (userRepository.findById(user.getId()) != null) {
                log.warn("Attempt to create user with an existing ID: {}", user.getId());
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            validationService.validateUser(user);
            userRepository.create(user);
            log.info("User with ID {} has been created.", user.getId());
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (ValidationException e) {
            log.error("User creation validation failed: {}", e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        } catch (ValidationNameException e) {
            return handleValidationNameException(user, true);
        }
    }

    @PutMapping()
    public ResponseEntity<User> update(@RequestBody User user) {
        try {
            if (userRepository.findById(user.getId()) == null) {
                log.warn("Attempt to update user with a non-existent ID: {}", user.getId());
                return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
            }
            validationService.validateUser(user);
            userRepository.update(user.getId(), user);
            log.info("User with ID {} has been updated.", user.getId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ValidationException e) {
            log.error("User update validation failed: {}", e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        } catch (ValidationNameException e) {
            return handleValidationNameException(user, false);
        }
    }

    private ResponseEntity<User> handleValidationNameException(User user, boolean isNew) {
        if (isNew) {
            user.setName(user.getLogin());
            userRepository.create(user);
            log.info("User with ID {} has been created with login as name.", user.getId());
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } else {
            user.setName(userRepository.findById(user.getId()).getName());
            userRepository.update(user.getId(), user);
            log.info("User with ID {} has been updated.", user.getId());
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }
}
