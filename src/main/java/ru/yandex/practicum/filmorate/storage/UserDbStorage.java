package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[] { "id" });
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        saveFriends(user.getId(), user.getFriends());
        return findById(user.getId());
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT u.*, f.friend_id " +
                "FROM users AS u " +
                "LEFT JOIN friendships AS f ON u.id = f.user_id " +
                "WHERE u.id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
            User user = null;
            Set<Integer> friends = new HashSet<>();

            while (rs.next()) {
                if (user == null) {
                    user = getUserMapper().mapRow(rs, rs.getRow());
                }
                int friendId = rs.getInt("friend_id");
                if (friendId != 0) {
                    friends.add(friendId);
                }
            }
            if (user != null) {
                user.setFriends(friends);
            }
            return user;
        });
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT u.*, f.friend_id " +
                "FROM users AS u " +
                "LEFT JOIN friendships AS f ON u.id = f.user_id";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, User> userMap = new HashMap<>();
            while (rs.next()) {
                int userId = rs.getInt("id");
                userMap.computeIfAbsent(userId, id -> {
                    try {
                        return getUserMapper().mapRow(rs, rs.getRow());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                User user = userMap.get(userId);
                int friendId = rs.getInt("friend_id");
                if (friendId != 0) {
                    user.getFriends().add(friendId);
                }
            }
            List<User> sortedUsers = new ArrayList<>(userMap.values());
            sortedUsers.sort(Comparator.comparingInt(User::getId));
            return sortedUsers;
        });
    }

    @Override
    @Transactional
    public User update(int id, User newUser) {
        String sql = "UPDATE users SET email = ?, " +
                "login = ?, " +
                "name = ?, " +
                "birthday = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                java.sql.Date.valueOf(newUser.getBirthday()),
                id);

        updateFriends(id, newUser.getFriends());
        return findById(id);
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }


    private static RowMapper<User> getUserMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());

            Set<Integer> friends = new HashSet<>();
            if (rs.getInt("friend_id") != 0) {
                friends.add(rs.getInt("friend_id"));
            }
            user.setFriends(friends);

            return user;
        };
    }

    private void saveFriends(int userId, Set<Integer> friends) {
        if (friends.size() > 0) {
            for (Integer id : friends) {
                String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?) ";
                jdbcTemplate.update(sql, userId, id);
            }
        }
    }

    private void updateFriends(int userId, Set<Integer> newFriends) {
        String sql = "DELETE FROM friendships WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
        saveFriends(userId, newFriends);
    }
}
