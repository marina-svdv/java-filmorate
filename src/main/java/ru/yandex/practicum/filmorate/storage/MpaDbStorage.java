package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa create(Mpa mpa) {
        String sqlQuery = "INSERT INTO mpa name VALUES ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[] { "id" });
            ps.setString(1, mpa.getName());
            return ps;
        }, keyHolder);
        mpa.setId(keyHolder.getKey().intValue());
        return findById(mpa.getId());
    }

    @Override
    public Mpa findById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        List<Mpa> result = jdbcTemplate.query(sql, new Object[]{id}, getMpaMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Mpa> findAll() {
        String sql = "SELECT * FROM mpa";

        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Mpa> mpaMap = new HashMap<>();

            while (rs.next()) {
                int mpaId = rs.getInt("id");
                if (!mpaMap.containsKey(mpaId)) {
                    Mpa mpa = getMpaMapper().mapRow(rs, rs.getRow());
                    mpaMap.put(mpaId, mpa);
                }
            }
            return new ArrayList<>(mpaMap.values());
        });
    }

    @Override
    public Mpa update(int id, Mpa newMpa) {
        String sql = "UPDATE mpa SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, newMpa.getName(), id);
        return findById(id);
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM mpa WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    private static RowMapper<Mpa> getMpaMapper() {
        return (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        };
    }
}
