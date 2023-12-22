package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {
    private Integer id;
    @NotBlank(message = "MPA name must not be blank")
    @Size(max = 6, message = "MPA name must be less than 6 characters")
    private String name;
}
