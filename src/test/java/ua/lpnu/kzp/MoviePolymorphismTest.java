package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Перевіряє, що спільний тип Movie обробляє підтипи без розгалуження за класом. */
class MoviePolymorphismTest {

    private static final double DELTA = 0.0001;

    @Test
    void sameInputsGiveDifferentScoresForDifferentSubtypes() {
        // Змінні базового типу: викликається реалізація фактичного підтипу.
        Movie feature = new FeatureFilm("А", "Б", 2000, 60, 8.0);
        Movie documentary = new Documentary("А", "Б", 2000, 60, 8.0);

        assertNotEquals(feature.recommendationScore(), documentary.recommendationScore());
        assertEquals(80.0, feature.recommendationScore(), DELTA);
        assertEquals(86.0, documentary.recommendationScore(), DELTA);
    }

    @Test
    void bestRecommendedWorksForMixedList() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new FeatureFilm("Художній", "А", 2000, 100, 8.0));      // 80.0
        movies.add(new Documentary("Документальний", "Б", 2010, 60, 8.0)); // 86.0
        movies.add(new FeatureFilm("Довгий", "В", 2005, 250, 9.0));        // 80.0

        Optional<Movie> best = MovieCatalogProcessor.bestRecommended(movies);

        assertTrue(best.isPresent());
        assertEquals("Документальний", best.get().getTitle());
    }

    @Test
    void bestRecommendedKeepsFirstOnTie() {
        Movie first = new FeatureFilm("Перший", "А", 2000, 90, 8.0);
        Movie second = new FeatureFilm("Другий", "Б", 2001, 90, 8.0);

        assertSame(first, MovieCatalogProcessor.bestRecommended(List.of(first, second)).get());
    }

    @Test
    void bestRecommendedOfEmptyListIsEmpty() {
        assertTrue(MovieCatalogProcessor.bestRecommended(List.of()).isEmpty());
    }

    @Test
    void bestRecommendedOfSingleMovieIsThatMovie() {
        Movie only = new Documentary("Єдиний", "А", 2000, 30, 5.0);

        assertSame(only, MovieCatalogProcessor.bestRecommended(List.of(only)).get());
    }

    @Test
    void countByKindIncludesZeroCounts() {
        Map<MovieKind, Integer> empty = MovieCatalogProcessor.countByKind(List.of());
        assertEquals(0, empty.get(MovieKind.FEATURE));
        assertEquals(0, empty.get(MovieKind.DOCUMENTARY));

        Map<MovieKind, Integer> counts = MovieCatalogProcessor.countByKind(List.of(
                new FeatureFilm("А", "Б", 2000, 90, 7.0),
                new FeatureFilm("В", "Г", 2001, 90, 7.0),
                new Documentary("Д", "Е", 2002, 90, 7.0)));
        assertEquals(2, counts.get(MovieKind.FEATURE));
        assertEquals(1, counts.get(MovieKind.DOCUMENTARY));
    }
}
