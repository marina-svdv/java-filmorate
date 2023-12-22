package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class User {
    private Integer id;
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Login must not be blank")
    @Pattern(regexp = "^\\S+$", message = "Login must not contain spaces")
    private String login;
    private String name;
    @Past(message = "Birthday must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
}