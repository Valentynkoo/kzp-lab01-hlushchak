package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Перевіряє підтип Documentary: стан, вид, бал і відхилення хибних полів. */
class DocumentaryTest {

    private static final double DELTA = 0.0001;

    @Test
    void storesFieldsAndHasDocumentaryKind() {
        Documentary film = new Documentary("Хроніка", "Автор", 2015, 60, 8.0);

        assertEquals("Хроніка", film.getTitle());
        assertEquals(2015, film.getYear());
        assertSame(MovieKind.DOCUMENTARY, film.getKind());
    }

    @Test
    void scoreAddsBonusForShortDuration() {
        // 60 хв дають бонус 6.0: 8.0 * 10 + 6.0.
        assertEquals(86.0, new Documentary("А", "Б", 2000, 60, 8.0).recommendationScore(), DELTA);
    }

    @Test
    void scoreBonusIsCappedAtBoundary() {
        double atCap = new Documentary("А", "Б", 2000, Documentary.BONUS_CAP_MINUTES, 8.0)
                .recommendationScore();
        double beyondCap = new Documentary("А", "Б", 2000, Documentary.BONUS_CAP_MINUTES + 100, 8.0)
                .recommendationScore();

        assertEquals(89.0, atCap, DELTA);
        assertEquals(atCap, beyondCap, DELTA);
    }

    @Test
    void scoreOfOneMinuteFilmGetsMinimalBonus() {
        assertEquals(50.1, new Documentary("А", "Б", 2000, 1, 5.0).recommendationScore(), DELTA);
    }

    @Test
    void rejectsInvalidFieldsLikeBaseClass() {
        assertThrows(NullPointerException.class, () -> new Documentary("А", null, 2000, 90, 7.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Documentary("А", "", 2000, 90, 7.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Documentary("А", "Б", 2000, -5, 7.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Documentary("А", "Б", 2000, 90, 10.5));
    }
}
