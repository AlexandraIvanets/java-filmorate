package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

class FilmControllerTest {
    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final String RELEASE_DATE_ERROR = "дата релиза — не раньше 28 декабря 1895 года";

    private FilmController filmController;

    @BeforeEach
    public void setup() {
        filmController = new FilmController();
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
    public void findAllReturnsEmptyListInitially() {
        Assertions.assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void createAssignsSequentialIds() {
        Film film1 = filmController.create(createValidFilm());
        Film film2 = filmController.create(createValidFilm());

        Assertions.assertEquals(1L, film1.getId());
        Assertions.assertEquals(2L, film2.getId());
        Assertions.assertEquals(2, filmController.findAll().size());
    }

    @Test
    public void createFilmWithFirstFilmReleaseDate() {
        Film film = createValidFilm();
        film.setReleaseDate(FIRST_FILM_RELEASE_DATE);

        Film created = filmController.create(film);

        Assertions.assertEquals(FIRST_FILM_RELEASE_DATE, created.getReleaseDate());
        Assertions.assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void createFilmWithReleaseDateDayAfterFirstFilm() {
        Film film = createValidFilm();
        film.setReleaseDate(FIRST_FILM_RELEASE_DATE.plusDays(1));

        filmController.create(film);

        Assertions.assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void createFilmWithReleaseDateDayBeforeFirstFilmIsRejected() {
        Film film = createValidFilm();
        film.setReleaseDate(FIRST_FILM_RELEASE_DATE.minusDays(1));

        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmController.create(film));

        Assertions.assertEquals(RELEASE_DATE_ERROR, exception.getMessage());
        Assertions.assertNull(film.getId());
        Assertions.assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void createFilmWithReleaseDateLongBeforeFirstFilmIsRejected() {
        LocalDate[] invalidDates = {LocalDate.of(1890, 3, 25), LocalDate.of(1, 1, 1)};

        for (LocalDate releaseDate : invalidDates) {
            Film film = createValidFilm();
            film.setReleaseDate(releaseDate);

            Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
        }
        Assertions.assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void createFilmWithoutReleaseDateIsRejected() {
        Film film = createValidFilm();
        film.setReleaseDate(null);

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
        Assertions.assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void updateChangesAllFields() {
        Film saved = filmController.create(createValidFilm());

        Film update = new Film();
        update.setId(saved.getId());
        update.setName("Updated name");
        update.setDescription("Updated description");
        update.setReleaseDate(LocalDate.of(1989, 4, 17));
        update.setDuration(190);

        Film updated = filmController.update(update);

        Assertions.assertEquals(saved.getId(), updated.getId());
        Assertions.assertEquals("Updated name", updated.getName());
        Assertions.assertEquals("Updated description", updated.getDescription());
        Assertions.assertEquals(LocalDate.of(1989, 4, 17), updated.getReleaseDate());
        Assertions.assertEquals(190, updated.getDuration());
        Assertions.assertEquals(1, filmController.findAll().size(), "Обновление не должно добавлять новый фильм");
    }

    @Test
    public void updateFilmWithFirstFilmReleaseDate() {
        Film saved = filmController.create(createValidFilm());

        Film update = createValidFilm();
        update.setId(saved.getId());
        update.setReleaseDate(FIRST_FILM_RELEASE_DATE);

        Film updated = filmController.update(update);

        Assertions.assertEquals(FIRST_FILM_RELEASE_DATE, updated.getReleaseDate());
    }

    @Test
    public void updateFilmWithReleaseDateBeforeFirstFilmIsRejected() {
        Film saved = filmController.create(createValidFilm());

        Film update = createValidFilm();
        update.setId(saved.getId());
        update.setName("New name");
        update.setReleaseDate(FIRST_FILM_RELEASE_DATE.minusDays(1));

        Assertions.assertThrows(ValidationException.class, () -> filmController.update(update));

        Film stored = filmController.findAll().iterator().next();
        Assertions.assertEquals("Film name", stored.getName());
        Assertions.assertEquals(LocalDate.of(2000, 1, 1), stored.getReleaseDate());
    }

    @Test
    public void updateUnknownFilmThrowsNotFound() {
        filmController.create(createValidFilm());

        Film update = createValidFilm();
        update.setId(999L);

        Assertions.assertThrows(NotFoundException.class, () -> filmController.update(update));
    }

    @Test
    public void createEmptyFilmIsRejected() {
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(new Film()));
        Assertions.assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void updateEmptyFilmIsRejected() {
        ConditionsNotMetException exception = Assertions.assertThrows(ConditionsNotMetException.class,
                () -> filmController.update(new Film()));

        Assertions.assertEquals("Id должен быть указан", exception.getMessage());
    }
}