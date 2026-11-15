package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Перевіряє узгоджені equals і hashCode: ключ фільму — клас, назва, режисер і рік. */
class MovieEqualityTest {

    private static Movie feature() {
        return new FeatureFilm("Земля", "Олександр Довженко", 1930, 75, 7.9);
    }

    @Test
    void equalMoviesHaveSameHashCode() {
        Movie a = feature();
        Movie b = feature();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsIsReflexiveSymmetricAndRejectsNullAndOtherTypes() {
        Movie a = feature();
        Movie b = feature();

        assertEquals(a, a);
        assertTrue(a.equals(b) && b.equals(a));
        assertFalse(a.equals(null));
        assertFalse(a.equals("Земля"));
    }

    @Test
    void ratingAndMinutesAreNotPartOfIdentity() {
        Movie original = new FeatureFilm("Земля", "Олександр Довженко", 1930, 75, 7.9);
        Movie reissue = new FeatureFilm("Земля", "Олександр Довженко", 1930, 80, 8.5);

        assertEquals(original, reissue);
        assertEquals(original.hashCode(), reissue.hashCode());
    }

    @Test
    void differentTitleDirectorOrYearMakesMoviesDifferent() {
        Movie base = feature();

        assertNotEquals(base, new FeatureFilm("Арсенал", "Олександр Довженко", 1930, 75, 7.9));
        assertNotEquals(base, new FeatureFilm("Земля", "Інший режисер", 1930, 75, 7.9));
        assertNotEquals(base, new FeatureFilm("Земля", "Олександр Довженко", 1931, 75, 7.9));
    }

    @Test
    void differentSubtypesWithSameKeyAreNotEqual() {
        Movie feature = new FeatureFilm("Земля", "Олександр Довженко", 1930, 75, 7.9);
        Movie documentary = new Documentary("Земля", "Олександр Довженко", 1930, 75, 7.9);

        assertNotEquals(feature, documentary);
        assertNotEquals(documentary, feature);
    }

    @Test
    void hashSetDoesNotDuplicateEqualMovies() {
        Set<Movie> movies = new HashSet<>();
        movies.add(feature());
        movies.add(feature());
        movies.add(new Documentary("Земля", "Олександр Довженко", 1930, 75, 7.9));

        // Два рівні художні фільми зливаються, документальний лишається окремим.
        assertEquals(2, movies.size());
    }

    @Test
    void movieWorksAsHashMapKey() {
        Map<Movie, String> notes = new HashMap<>();
        notes.put(feature(), "перша копія");
        notes.put(feature(), "друга копія");

        assertEquals(1, notes.size());
        assertEquals("друга копія", notes.get(feature()));
    }
}
