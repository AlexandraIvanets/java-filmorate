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
class FilmValidationTest {
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

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }

    @Test
    public void validFilmHasNoViolations() {
        Set<ConstraintViolation<Film>> violations = validator.validate(createValidFilm());

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void blankNameIsInvalid() {
        String[] blankNames = {null, "", " "};

        for (String name : blankNames) {
            Film film = createValidFilm();
            film.setName(name);

            Assertions.assertFalse(validator.validate(film).isEmpty());
        }
    }

    @Test
    public void description200CharsIsValid() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(200));

        Assertions.assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    public void description201CharsIsInvalid() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void durationOneIsValid() {
        Film film = createValidFilm();
        film.setDuration(1);

        Assertions.assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    public void durationZeroIsInvalid() {
        Film film = createValidFilm();
        film.setDuration(0);

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void negativeDurationIsInvalid() {
        Film film = createValidFilm();
        film.setDuration(-1);

        Assertions.assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    public void emptyFilmIsInvalid() {
        Assertions.assertFalse(validator.validate(new Film()).isEmpty());
    }
}
