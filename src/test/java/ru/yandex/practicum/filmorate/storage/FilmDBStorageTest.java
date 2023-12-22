package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
public class FilmDBStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;

    @BeforeEach
    public void setUp() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    @Test
    public void testCreateAndGetValidFilm() {
        Film film = new Film();
        film.setName("Home Alone");
        film.setDescription("A family comedy without the family.");
        film.setReleaseDate(LocalDate.of(1990, 11, 10));
        film.setDuration(103);
        film.setMpa(new Mpa());
        film.getMpa().setId(2);
        Genre genre = new Genre();
        genre.setId(1);
        film.getGenres().add(genre);

        int id = (filmDbStorage.create(film)).getId();
        Film res = filmDbStorage.findById(id);

        film.getMpa().setName("PG");
        Genre expectedGenre = new Genre();
        expectedGenre.setId(1);
        expectedGenre.setName("Комедия");
        film.setGenres(Collections.singletonList(expectedGenre));

        assertThat(res).usingRecursiveComparison().isEqualTo(film);
    }

    @Test
    public void testGetFilmByIdWithInvalidId() {
        int invalidId = -1;
        Film result = filmDbStorage.findById(invalidId);
        assertThat(result).isNull();
    }

    @Test
    public void testUpdateValidFilm() {
        Film film = new Film();
        film.setName("Home Alone");
        film.setDescription("A family comedy without the family.");
        film.setReleaseDate(LocalDate.of(1990, 11, 10));
        film.setDuration(103);
        film.setMpa(new Mpa());
        film.getMpa().setId(2);
        Genre genre = new Genre();
        genre.setId(1);
        film.getGenres().add(genre);
        filmDbStorage.create(film);

        film.setName("Home Updated");
        film.setReleaseDate(LocalDate.of(2000, 11, 10));
        film.setDuration(93);

        Film updatedFilm = filmDbStorage.update(film.getId(), film);

        assertThat(updatedFilm.getName()).isEqualTo("Home Updated");
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2000, 11, 10));
        assertThat(updatedFilm.getDuration()).isEqualTo(93);
    }

    @Test
    public void testUpdateNonExistingFilm() {
        Film film = new Film();
        film.setId(1111);
        film.setName("Nonexistent Film");
        film.setDescription("A test film description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        assertThat(filmDbStorage.update(film.getId(), film)).isNull();
    }

    @Test
    public void testFindAllFilms() {
        Film film = new Film();
        film.setName("Home Alone");
        film.setDescription("A family comedy without the family.");
        film.setReleaseDate(LocalDate.of(1990, 11, 10));
        film.setDuration(103);
        film.setMpa(new Mpa());
        film.getMpa().setId(2);
        Genre genre = new Genre();
        genre.setId(1);
        film.getGenres().add(genre);
        filmDbStorage.create(film);

        Film film1 = new Film();
        film1.setName(" A Christmas Story");
        film1.setDescription("Sometimes Christmas is about getting what you really want.");
        film1.setReleaseDate(LocalDate.of(1983, 11, 18));
        film1.setDuration(94);
        filmDbStorage.create(film1);

        List<Film> films = filmDbStorage.findAll();

        assertThat(films).isNotNull();
        assertThat(films.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testFindAllFilmsEmpty() {
        List<Film> films = filmDbStorage.findAll();

        assertThat(films.isEmpty());
    }

    @Test
    public void testDeleteValidFilm() {
        Film film = new Film();
        film.setName("Home Alone");
        film.setDescription("A family comedy without the family.");
        film.setReleaseDate(LocalDate.of(1990, 11, 10));
        film.setDuration(103);
        film.setMpa(new Mpa());
        film.getMpa().setId(2);
        Genre genre = new Genre();
        genre.setId(1);
        film.getGenres().add(genre);
        filmDbStorage.create(film);

        boolean isDeleted = filmDbStorage.delete(film.getId());

        assertThat(isDeleted).isTrue();
        assertThat(filmDbStorage.findById(film.getId())).isNull();
    }

    @Test
    public void testUpdateWithNonExistingReferences() {
        Film film = new Film();
        film.setName("Existing Film");
        film.setDescription("A film with existing references.");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Genre existingGenre = new Genre();
        existingGenre.setId(1);
        existingGenre.setName("Комедия");
        film.getGenres().add(existingGenre);

        int filmId = (filmDbStorage.create(film)).getId();

        // Обновление фильма с несуществующими ссылками
        film.setId(filmId);
        film.setMpa(new Mpa(999, "Nonexistent rate"));

        // Проверка генерации исключения при попытке обновления
        assertThatThrownBy(() -> filmDbStorage.update(filmId, film))
                .isInstanceOf(DataAccessException.class);

        // Проверить, что данные фильма не изменились
        Film unchangedFilm = filmDbStorage.findById(filmId);
        assertThat(unchangedFilm.getMpa().getId()).isNotEqualTo(999);
    }
}
