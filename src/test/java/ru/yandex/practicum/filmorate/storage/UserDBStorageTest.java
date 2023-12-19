package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
public class UserDBStorageTest {

    private final JdbcTemplate jdbcTemplate;
    UserDbStorage userDbStorage;

    @BeforeEach
    public void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testCreateAndGetValidUserById() {
        User user = new User();
        user.setEmail("bob.kelso@sacredheart.com");
        user.setLogin("kelso");
        user.setName("Robert");
        user.setBirthday(LocalDate.of(1949, 1, 1));
        int id = (userDbStorage.create(user)).getId();

        User res = userDbStorage.findById(id);

        assertThat(res)
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void testCreateUserWithFriends() {
        User user = new User();
        user.setEmail("bob.kelso@sacredheart.com");
        user.setLogin("kelso");
        user.setName("Robert");
        user.setBirthday(LocalDate.of(1949, 1, 1));
        int userId = userDbStorage.create(user).getId();

        User user1 = new User();
        user1.setEmail("perry.cox@sacredheart.com");
        user1.setLogin("perry");
        user1.setName("Peregrine");
        user1.setBirthday(LocalDate.of(1968, 1, 1));
        int userId1 = userDbStorage.create(user1).getId();

        User user2 =new User();
        user2.setEmail("vanillabear.dorian@sacredheart.com");
        user2.setLogin("J.D.");
        user2.setName("John Dorian");
        user2.setBirthday(LocalDate.of(1985, 6, 22));
        Set<Integer> friends = Set.of(userId, userId1);
        user2.setFriends(friends);
        userDbStorage.create(user2);

        assertThat(user2.getFriends()).isEqualTo(friends);
    }

    @Test
    public void testGetUserByIdWithInvalidId() {
        int invalidId = -1;

        User result = userDbStorage.findById(invalidId);

        assertThat(result).isNull();
    }

    @Test
    public void testUpdateValidUser() {
        User user = new User();
        user.setEmail("bob.kelso@sacredheart.com");
        user.setLogin("kelso");
        user.setName("Robert");
        user.setBirthday(LocalDate.of(1949, 1, 1));
        int id = (userDbStorage.create(user)).getId();

        user.setName("Bob");
        user.setBirthday(LocalDate.of(1947, 1, 1));

        User updatedUser = userDbStorage.update(id, user);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Bob");
        assertThat(updatedUser.getBirthday()).isEqualTo(LocalDate.of(1947, 1, 1));
    }

    @Test
    public void testUpdateNonExistingUser() {
        User user = new User();
        user.setId(-1);
        user.setEmail("nonexistent@example.com");
        user.setLogin("nonexistent");
        user.setName("Nonexistent");
        user.setBirthday(LocalDate.now());

        assertThat(userDbStorage.update(user.getId(), user)).isNull();
    }

    @Test
    public void testFindAllUsers() {
        User user = new User();
        user.setEmail("bob.kelso@sacredheart.com");
        user.setLogin("kelso");
        user.setName("Robert");
        user.setBirthday(LocalDate.of(1949, 1, 1));
        userDbStorage.create(user);

        User user1 = new User();
        user1.setEmail("perry.cox@sacredheart.com");
        user1.setLogin("perry");
        user1.setName("Peregrine");
        user1.setBirthday(LocalDate.of(1968, 1, 1));
        userDbStorage.create(user1);

        List<User> users = userDbStorage.findAll();

        assertThat(users).isNotNull();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testFindAllUsersEmpty() {
        List<User> users = userDbStorage.findAll();

        assertThat(users.isEmpty());
    }

    @Test
    public void testDeleteValidUser() {
        User user = new User();
        user.setEmail("bob.kelso@sacredheart.com");
        user.setLogin("kelso");
        user.setName("Robert");
        user.setBirthday(LocalDate.of(1949, 1, 1));
        userDbStorage.create(user);

        boolean isDeleted = userDbStorage.delete(user.getId());

        assertThat(isDeleted).isTrue();
        assertThat(userDbStorage.findById(user.getId())).isNull();
    }
}
