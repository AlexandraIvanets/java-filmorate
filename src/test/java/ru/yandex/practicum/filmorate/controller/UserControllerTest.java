package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    public void setup() {
        userController = new UserController();
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    public void getAllUsersFor0() {
        int expectedAmountOfUsers = 0;
        int actualAmountOfUsers = userController.findAll().size();
        Assertions.assertEquals(expectedAmountOfUsers, actualAmountOfUsers);
    }

    @Test
    public void getAllUsers() {
        userController.create(createValidUser());
        userController.create(createValidUser());

        int expectedAmountOfUsers = 2;
        int actualAmountOfUsers = userController.findAll().size();
        Assertions.assertEquals(expectedAmountOfUsers, actualAmountOfUsers);
    }

    @Test
    public void getNextId() {
        User user1 = userController.create(createValidUser());
        User user2 = userController.create(createValidUser());

        Long expectedId1 = 1L;
        Long expectedId2 = 2L;

        Assertions.assertEquals(expectedId1, user1.getId());
        Assertions.assertEquals(expectedId2, user2.getId());
    }

    @Test
    public void createUserWithNameKeepsName() {
        User created = userController.create(createValidUser());

        Assertions.assertEquals("Name", created.getName());
    }

    @Test
    public void createUserWithBlankNameUsesLogin() {
        String[] blankNames = {null, "", " "};

        for (String name : blankNames) {
            User user = createValidUser();
            user.setName(name);

            User created = userController.create(user);

            Assertions.assertEquals("login", created.getName());
        }
    }

    @Test
    public void updateChangesAllFields() {
        User saved = userController.create(createValidUser());

        User update = new User();
        update.setId(saved.getId());
        update.setEmail("new@yandex.ru");
        update.setLogin("newLogin");
        update.setName("New Name");
        update.setBirthday(LocalDate.of(1976, 9, 20));

        User updated = userController.update(update);

        Assertions.assertEquals(saved.getId(), updated.getId());
        Assertions.assertEquals("new@yandex.ru", updated.getEmail());
        Assertions.assertEquals("newLogin", updated.getLogin());
        Assertions.assertEquals("New Name", updated.getName());
        Assertions.assertEquals(LocalDate.of(1976, 9, 20), updated.getBirthday());
        Assertions.assertEquals(1, userController.findAll().size());
    }

    @Test
    public void updateUserWithBlankNameUsesNewLogin() {
        User saved = userController.create(createValidUser());

        User update = createValidUser();
        update.setId(saved.getId());
        update.setLogin("newLogin");
        update.setName("");

        User updated = userController.update(update);

        Assertions.assertEquals("newLogin", updated.getName());
    }

    @Test
    public void updateUnknownUserThrowsNotFound() {
        userController.create(createValidUser());

        User update = createValidUser();
        update.setId(999L);

        Assertions.assertThrows(NotFoundException.class, () -> userController.update(update));
    }

    @Test
    public void updateEmptyUserIsRejected() {
        ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> userController.update(new User()));

        Assertions.assertEquals("Id должен быть указан", exception.getMessage());
    }
}