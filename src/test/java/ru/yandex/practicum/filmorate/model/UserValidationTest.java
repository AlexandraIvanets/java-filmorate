package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

//I don't know how to check POJO's validation if I use @valid, so I created validator
class UserValidationTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public static void closeValidator() {
        factory.close();
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
    public void validUserHasNoViolations() {
        Set<ConstraintViolation<User>> violations = validator.validate(createValidUser());

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void blankEmailIsInvalid() {
        String[] blankEmails = {null, "", " "};

        for (String email : blankEmails) {
            User user = createValidUser();
            user.setEmail(email);

            Assertions.assertFalse(validator.validate(user).isEmpty());
        }
    }

    @Test
    public void incorrectEmailIsInvalid() {
        String[] incorrectEmails = {"mail.ru", "mail@", "@mail.ru"};

        for (String email : incorrectEmails) {
            User user = createValidUser();
            user.setEmail(email);

            Assertions.assertFalse(validator.validate(user).isEmpty());
        }
    }

    @Test
    public void blankLoginIsInvalid() {
        String[] blankLogins = {null, "", " "};

        for (String login : blankLogins) {
            User user = createValidUser();
            user.setLogin(login);

            Assertions.assertFalse(validator.validate(user).isEmpty());
        }
    }

    @Test
    public void loginWithSpaceIsInvalid() {
        User user = createValidUser();
        user.setLogin("my login");

        Assertions.assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    public void emptyNameIsValid() {
        User user = createValidUser();
        user.setName("");

        Assertions.assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    public void birthdayTodayIsValid() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now());

        Assertions.assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    public void birthdayTomorrowIsInvalid() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        Assertions.assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    public void emptyUserIsInvalid() {
        Assertions.assertFalse(validator.validate(new User()).isEmpty());
    }
}
