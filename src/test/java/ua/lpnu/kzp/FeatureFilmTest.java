package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Перевіряє підтип FeatureFilm: стан, вид, бал і відхилення хибних полів. */
class FeatureFilmTest {

    private static final double DELTA = 0.0001;

    @Test
    void storesFieldsAndHasFeatureKind() {
        FeatureFilm film = new FeatureFilm("Земля", "Олександр Довженко", 1930, 75, 7.9);

        assertEquals("Земля", film.getTitle());
        assertEquals(1930, film.getYear());
        assertSame(MovieKind.FEATURE, film.getKind());
    }

    @Test
    void scoreWithoutPenaltyIsRatingTimesWeight() {
        // 75 хв не перевищують 150, штрафу немає: 7.9 * 10.
        assertEquals(79.0, new FeatureFilm("Земля", "Довженко", 1930, 75, 7.9)
                .recommendationScore(), DELTA);
    }

    @Test
    void scoreAtComfortableBoundaryHasNoPenalty() {
        FeatureFilm boundary = new FeatureFilm("А", "Б", 2000,
                FeatureFilm.COMFORTABLE_MINUTES, 8.0);

        assertEquals(80.0, boundary.recommendationScore(), DELTA);
    }

    @Test
    void scoreOneMinuteOverBoundaryGetsSmallPenalty() {
        FeatureFilm over = new FeatureFilm("А", "Б", 2000,
                FeatureFilm.COMFORTABLE_MINUTES + 1, 8.0);

        assertEquals(80.0 - FeatureFilm.PENALTY_PER_MINUTE, over.recommendationScore(), DELTA);
    }

    @Test
    void scoreOfVeryLongFilmGetsLargePenalty() {
        // 250 хв: 100 зайвих хвилин, штраф 10.0.
        assertEquals(60.0, new FeatureFilm("А", "Б", 2000, 250, 7.0).recommendationScore(),
                DELTA);
    }

    @Test
    void rejectsInvalidFieldsLikeBaseClass() {
        assertThrows(NullPointerException.class, () -> new FeatureFilm(null, "Б", 2000, 90, 7.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFilm("  ", "Б", 2000, 90, 7.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFilm("А", "Б", 2000, 0, 7.0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFilm("А", "Б", 2000, 90, Double.NaN));
    }
}
