package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[] { "id" });
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> sortedGenres = film.getGenres().stream()
                    .sorted(Comparator.comparingInt(Genre::getId))
                    .collect(Collectors.toList());
            saveGenres(film.getId(), sortedGenres);
        }

        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            saveLikes(film.getId(), film.getLikes());
        }
        return film;
    }

    @Override
    @Transactional
    public Film update(int id, Film newFilm) {
        String sql = "UPDATE films SET name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa_id = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newFilm.getName());
            ps.setString(2, newFilm.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(newFilm.getReleaseDate()));
            ps.setInt(4, newFilm.getDuration());
            if (newFilm.getMpa() != null && newFilm.getMpa().getId() != null) {
                ps.setInt(5, newFilm.getMpa().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setInt(6, id);
            return ps;
        });
        updateGenres(id, newFilm.getGenres());
        updateLikes(id, newFilm.getLikes());

        return findById(id);
    }

    @Override
    public Film findById(int id) {
        String sql = "SELECT f.*, " +
                "m.name AS mpa_name," +
                "g.id AS genre_id, " +
                "g.name AS genre_name, " +
                "l.user_id AS user_id_like " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.id " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "WHERE f.id = ?";

        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            Film film = null;
            List<Genre> genres = new ArrayList<>();
            Set<Integer> likes = new HashSet<>();

            while (rs.next()) {
                if (film == null) {
                    film = getFilmMapper().mapRow(rs, rs.getRow());
                }
                int genreId = rs.getInt("genre_id");
                if (genreId != 0) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    genres.add(genre);
                }
                int userIdLike = rs.getInt("user_id_like");
                if (userIdLike != 0) {
                    likes.add(userIdLike);
                }
            }

            if (film != null) {
                film.setGenres(genres);
                film.setLikes(likes);
            }
            return film;
        });
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*," +
                "m.name AS mpa_name, " +
                "g.id AS genre_id, " +
                "g.name AS genre_name, " +
                "l.user_id  AS user_id_like " +
                "FROM films f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.id " +
                "LEFT JOIN likes AS l ON f.id = l.film_id";

        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Film> filmMap = new HashMap<>();

            while (rs.next()) {
                int filmId = rs.getInt("id");
                // computeIfAbsent для создания нового объекта Film, если он еще не существует в filmMap
                Film film = filmMap.computeIfAbsent(filmId, id -> {
                    try {
                        return getFilmMapper().mapRow(rs, rs.getRow());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                assert film != null;
                Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());

                int genreId = rs.getInt("genre_id");
                if (genreId != 0) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    uniqueGenres.add(genre); // Добавляем новый жанр, если он не дублируется
                }

                film.setGenres(new ArrayList<>(uniqueGenres));

                int userIdLike = rs.getInt("user_id_like");
                if (userIdLike != 0) {
                    film.getLikes().add(userIdLike);
                }
            }

            filmMap.values().forEach(film -> {
                film.getGenres().sort(Comparator.comparingInt(Genre::getId));
            });

            return new ArrayList<>(filmMap.values());
        });
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }


    private static RowMapper<Film> getFilmMapper() {
        return (rs, rowNum) -> {
           Film film = new Film();
           film.setId(rs.getInt("id"));
           film.setName(rs.getString("name"));
           film.setDescription(rs.getString("description"));
           film.setReleaseDate(rs.getDate("release_date").toLocalDate());
           film.setDuration(rs.getInt("duration"));

           Mpa mpa = new Mpa();
           if (rs.getInt("mpa_id") != 0) {
              mpa.setId(rs.getInt("mpa_id"));
              mpa.setName(rs.getString("mpa_name"));
           }
           film.setMpa(mpa);

           List<Genre> genres = new ArrayList<>();
           if (rs.getInt("genre_id") != 0) {
               Genre genre = new Genre();
               genre.setId(rs.getInt("genre_id"));
               genre.setName(rs.getString("genre_name"));
               genres.add(genre);
           }
           film.setGenres(genres);

           Set<Integer> likes = new HashSet<>();
           if (rs.getInt("user_id_like") != 0) {
               likes.add(rs.getInt("user_id_like"));
           }
           film.setLikes(likes);

           return film;
       };
    }

    private void saveGenres(int filmId, List<Genre> genres) {
        Set<Genre> uniqueGenres = new HashSet<>(genres);
        List<Genre> sortedGenres = new ArrayList<>(uniqueGenres);
        sortedGenres.sort(Comparator.comparingInt(Genre::getId));

        if (sortedGenres.size() > 0) {
            for (Genre genre : genres) {
                String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?) ";
                jdbcTemplate.update(sql, filmId, genre.getId());
            }
        }
    }

    private void updateGenres(int filmId, List<Genre> newGenres) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
        List<Genre> uniqueSortedGenres = new ArrayList<>(new LinkedHashSet<>(newGenres));
        uniqueSortedGenres.sort(Comparator.comparingInt(Genre::getId));
        saveGenres(filmId, uniqueSortedGenres);
    }

    private void saveLikes(int filmId, Set<Integer> likes) {
        if (likes.size() > 0) {
            for (Integer userIdLike : likes) {
                String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?) ";
                jdbcTemplate.update(sql, userIdLike, filmId);
            }
        }
    }

    private void updateLikes(int filmId, Set<Integer> newLikes) {
        String sql = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
        saveLikes(filmId, newLikes);
    }
}
