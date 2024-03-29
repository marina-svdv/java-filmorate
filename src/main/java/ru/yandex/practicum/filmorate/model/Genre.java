package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class Genre {
    private Integer id;
    @NotBlank(message = "Genre name must not be blank")
    @Size(max = 40, message = "Genre name must be less than 40 characters")
    private String name;
}
