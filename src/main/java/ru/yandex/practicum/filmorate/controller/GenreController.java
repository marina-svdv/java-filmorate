package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getAllGenres() {
        return new ArrayList<>(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreService.getGenreById(id);
    }

    @PostMapping()
    public Genre createGenre(@Valid @RequestBody Genre genre) {
        return genreService.createGenre(genre);
    }

    @PutMapping()
    public Genre updateRate(@Valid @RequestBody Genre genre) {
        return genreService.updateGenre(genre);
    }
}
