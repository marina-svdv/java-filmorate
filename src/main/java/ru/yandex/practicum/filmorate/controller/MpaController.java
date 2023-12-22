package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> getAllRates() {
        return new ArrayList<>(mpaService.getAllRates());
    }

    @GetMapping("/{id}")
    public Mpa getRateById(@PathVariable int id) {
        return mpaService.getRateById(id);
    }

    @PostMapping()
    public Mpa createRate(@Valid @RequestBody Mpa mpa) {
        return mpaService.createRate(mpa);
    }

    @PutMapping()
    public Mpa updateRate(@Valid @RequestBody Mpa mpa) {
        return mpaService.updateRate(mpa);
    }
}
