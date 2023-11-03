package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class User {
    private static int COUNTER = 1;

    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public void assignId() {
        if (id == 0) {
            id = COUNTER++;
        }
    }
}