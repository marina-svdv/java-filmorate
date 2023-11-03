package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private static int COUNTER = 1;

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int rate;

    public Film() {
        if (id == 0) {
            id = COUNTER++;
        }
    }
}