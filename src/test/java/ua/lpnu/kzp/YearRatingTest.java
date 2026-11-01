package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Перевіряє record YearRating: доступ до компонентів, рівність і валідацію. */
class YearRatingTest {

    private static final double DELTA = 0.0001;

    @Test
    void componentsAreAvailableByName() {
        YearRating pair = new YearRating(1930, 7.9);

        assertEquals(1930, pair.year());
        assertEquals(7.9, pair.rating(), DELTA);
    }

    @Test
    void recordsWithSameValuesAreEqual() {
        YearRating first = new YearRating(1930, 7.9);
        YearRating second = new YearRating(1930, 7.9);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void acceptsBoundaryRatings() {
        assertEquals(0.0, new YearRating(2000, 0.0).rating(), DELTA);
        assertEquals(10.0, new YearRating(2000, 10.0).rating(), DELTA);
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> new YearRating(-1, 7.0));
        assertThrows(IllegalArgumentException.class, () -> new YearRating(0, 7.0));
        assertThrows(IllegalArgumentException.class, () -> new YearRating(2000, -0.5));
        assertThrows(IllegalArgumentException.class, () -> new YearRating(2000, 10.5));
        assertThrows(IllegalArgumentException.class, () -> new YearRating(2000, Double.NaN));
    }
}
