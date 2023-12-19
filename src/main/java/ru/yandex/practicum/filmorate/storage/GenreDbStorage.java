package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre create(Genre genre) {
        String sqlQuery = "INSERT INTO genres (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[] { "id" });
            ps.setString(1, genre.getName());
            return ps;
        }, keyHolder);
        genre.setId(keyHolder.getKey().intValue());
        return findById(genre.getId());
    }

    @Override
    public Genre findById(int id) {
        String sql = "SELECT * " +
                "FROM genres " +
                "WHERE genres.id = ?";
        List<Genre> result = jdbcTemplate.query(sql, new Object[]{id}, getGenreMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres";

        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Genre> genresMap = new HashMap<>();

            while (rs.next()) {
                int genreId = rs.getInt("id");
                if (!genresMap.containsKey(genreId)) {
                    Genre genre = getGenreMapper().mapRow(rs, rs.getRow());
                    genresMap.put(genreId, genre);
                }
            }
            return new ArrayList<>(genresMap.values());
        });
    }

    @Override
    public Genre update(int id, Genre newGenre) {
        String sql = "UPDATE genres SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, newGenre.getName(), id);
        return findById(id);
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM genres WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    private static RowMapper<Genre> getGenreMapper() {
        return (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        };
    }
}
