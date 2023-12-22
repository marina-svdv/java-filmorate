package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllRates() {
        return new ArrayList<>(mpaStorage.findAll());
    }

    public Mpa getRateById(int id) {
        Mpa mpa = mpaStorage.findById(id);
        if (mpa == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rate not found");
        }
        return mpa;
    }

    public Mpa createRate(Mpa mpa) {
        if (mpaStorage.findById(mpa.getId()) != null) {
            log.warn("Attempt to create MPA rate with an existing ID: {}", mpa.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Rate with given ID already exists");
        }
        Mpa  createdMpa = mpaStorage.create(mpa);
        log.info("Rate with ID {} has been created.", mpa.getId());
        return createdMpa;
    }

    public Mpa updateRate(Mpa newMpa) {
        Mpa updatedMpa;
        if (mpaStorage.findById(newMpa.getId()) == null) {
            log.warn("Attempt to update non-existing Mpa rate with ID: {}", newMpa.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mpa rate not found");
        }
        updatedMpa = mpaStorage.update(newMpa.getId(), newMpa);
        log.info("Rate with ID {} has been updated.", newMpa.getId());
        return updatedMpa;
    }
}
