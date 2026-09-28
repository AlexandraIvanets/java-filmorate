package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Find all films, in total: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Create new film: {}", film);
        isReleaseDateValid(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Created film: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.debug("Update film: {}", newFilm);
        if (newFilm.getId() == null) {
            log.warn("New film id is null");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            log.debug("Film exists");
            isReleaseDateValid(newFilm);
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Updated film: {}", oldFilm);
            return oldFilm;
        }
        log.warn("New film's id wasn't found");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++currentMaxId;
    }

    private void isReleaseDateValid(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            log.warn("Film's release date is before 1895/12/28");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}
