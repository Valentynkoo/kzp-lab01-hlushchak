package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Перевіряє enum MovieKind: коди, підписи, пошук за кодом і створення підтипів. */
class MovieKindTest {

    @Test
    void hasFixedSetOfValuesWithCodesAndLabels() {
        assertEquals(2, MovieKind.values().length);
        assertEquals("feature", MovieKind.FEATURE.code());
        assertEquals("художній", MovieKind.FEATURE.label());
        assertEquals("documentary", MovieKind.DOCUMENTARY.code());
        assertEquals("документальний", MovieKind.DOCUMENTARY.label());
    }

    @Test
    void fromCodeFindsKindIgnoringCaseAndSpaces() {
        assertSame(MovieKind.DOCUMENTARY, MovieKind.fromCode("documentary"));
        assertSame(MovieKind.DOCUMENTARY, MovieKind.fromCode("  DocuMentary "));
        assertSame(MovieKind.FEATURE, MovieKind.fromCode("feature"));
    }

    @Test
    void fromCodeRejectsUnknownEmptyAndNullCode() {
        IllegalArgumentException unknown = assertThrows(IllegalArgumentException.class,
                () -> MovieKind.fromCode("cartoon"));
        assertEquals("невідомий вид фільму: cartoon", unknown.getMessage());
        assertThrows(IllegalArgumentException.class, () -> MovieKind.fromCode(""));
        assertThrows(NullPointerException.class, () -> MovieKind.fromCode(null));
    }

    @Test
    void createBuildsSubtypeOfMatchingKind() {
        Movie feature = MovieKind.FEATURE.create("А", "Б", 2000, 90, 7.0);
        Movie documentary = MovieKind.DOCUMENTARY.create("А", "Б", 2000, 90, 7.0);

        assertInstanceOf(FeatureFilm.class, feature);
        assertInstanceOf(Documentary.class, documentary);
        assertSame(MovieKind.FEATURE, feature.getKind());
        assertSame(MovieKind.DOCUMENTARY, documentary.getKind());
    }

    @Test
    void createPassesInvariantViolationFromBaseClass() {
        assertThrows(IllegalArgumentException.class,
                () -> MovieKind.DOCUMENTARY.create("А", "Б", 2000, -1, 7.0));
    }
}
