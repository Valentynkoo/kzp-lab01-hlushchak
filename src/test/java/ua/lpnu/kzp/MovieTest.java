package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Перевіряє інваріанти спільного типу Movie (через підтип FeatureFilm), фабрику
 * fromCsv і toString. Раніше тести створювали Movie напряму; після того як Movie
 * став абстрактним, вони створюють FeatureFilm із тими самими значеннями.
 */
class MovieTest {

    private static final double DELTA = 0.0001;

    // ---------- конструктор ----------

    @Test
    void constructorStoresAllFields() {
        // Act
        Movie movie = new FeatureFilm("Земля", "Олександр Довженко", 1930, 75, 7.9);

        // Assert
        assertEquals("Земля", movie.getTitle());
        assertEquals("Олександр Довженко", movie.getDirector());
        assertEquals(1930, movie.getYear());
        assertEquals(75, movie.getMinutes());
        assertEquals(7.9, movie.getRating(), DELTA);
    }

    @Test
    void constructorAcceptsBoundaryRatings() {
        // Межі діапазону 0 і 10 допустимі.
        assertEquals(0.0, new FeatureFilm("А", "Б", 2000, 90, 0.0).getRating(), DELTA);
        assertEquals(10.0, new FeatureFilm("А", "Б", 2000, 90, 10.0).getRating(), DELTA);
    }

    @Test
    void constructorAcceptsMinimalPositiveYearAndMinutes() {
        Movie movie = new FeatureFilm("А", "Б", 1, 1, 5.0);

        assertEquals(1, movie.getYear());
        assertEquals(1, movie.getMinutes());
    }

    @Test
    void constructorRejectsNullTitleAndDirector() {
        assertThrows(NullPointerException.class, () -> new FeatureFilm(null, "Б", 2000, 90, 7.0));
        assertThrows(NullPointerException.class, () -> new FeatureFilm("А", null, 2000, 90, 7.0));
    }

    @Test
    void constructorRejectsBlankTitle() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new FeatureFilm("   ", "Б", 2000, 90, 7.0));

        assertEquals("порожня назва фільму", error.getMessage());
    }

    @Test
    void constructorRejectsBlankDirector() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new FeatureFilm("А", "", 2000, 90, 7.0));

        assertEquals("порожнє ім'я режисера", error.getMessage());
    }

    @Test
    void constructorRejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> new FeatureFilm("А", "Б", -1, 90, 7.0));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFilm("А", "Б", 2000, -45, 7.0));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFilm("А", "Б", 2000, 90, -0.1));
    }

    @Test
    void constructorRejectsZeroYearAndZeroMinutes() {
        assertThrows(IllegalArgumentException.class, () -> new FeatureFilm("А", "Б", 0, 90, 7.0));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFilm("А", "Б", 2000, 0, 7.0));
    }

    @Test
    void constructorRejectsRatingOutOfRangeAndNonFiniteRating() {
        assertThrows(IllegalArgumentException.class, () -> new FeatureFilm("А", "Б", 2000, 90, 10.1));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFilm("А", "Б", 2000, 90, Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFilm("А", "Б", 2000, 90, Double.POSITIVE_INFINITY));
    }

    // ---------- fromCsv ----------

    @Test
    void fromCsvParsesValidLine() {
        Movie movie = Movie.fromCsv("Тіні забутих предків;Сергій Параджанов;1965;97;8.1");

        assertEquals("Тіні забутих предків", movie.getTitle());
        assertEquals("Сергій Параджанов", movie.getDirector());
        assertEquals(1965, movie.getYear());
        assertEquals(97, movie.getMinutes());
        assertEquals(8.1, movie.getRating(), DELTA);
    }

    @Test
    void fromCsvTrimsSpacesAroundFields() {
        Movie movie = Movie.fromCsv(" Земля ; Олександр Довженко ; 1930 ; 75 ; 7.9 ");

        assertEquals("Земля", movie.getTitle());
        assertEquals("Олександр Довженко", movie.getDirector());
        assertEquals(1930, movie.getYear());
    }

    @Test
    void fromCsvRejectsWrongFieldCount() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Movie.fromCsv("Позивний Бандерас;Богдан Слюсаревський;2018;99"));

        assertEquals("очікується 5 полів, отримано 4", error.getMessage());
    }

    @Test
    void fromCsvRejectsNonNumericField() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Movie.fromCsv("Земля кличе;Іван Кравченко;19XX;95;7.0"));

        assertEquals("числове поле має помилковий формат", error.getMessage());
    }

    @Test
    void fromCsvDoesNotDropEmptyLastField() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Movie.fromCsv("Земля;Олександр Довженко;1930;75;"));

        assertEquals("числове поле має помилковий формат", error.getMessage());
    }

    @Test
    void fromCsvRejectsBlankLineAndNull() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Movie.fromCsv("  "));

        assertEquals("порожній рядок", error.getMessage());
        assertThrows(NullPointerException.class, () -> Movie.fromCsv(null));
    }

    @Test
    void fromCsvPassesInvariantViolationFromConstructor() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Movie.fromCsv("Мій Карабах;Тарас Ткаченко;2020;-45;5.5"));

        assertEquals("від'ємне числове значення", error.getMessage());
    }

    // ---------- fromCsv: необов'язкове шосте поле «вид» ----------

    @Test
    void fromCsvWithoutKindCreatesFeatureFilm() {
        Movie movie = Movie.fromCsv("Земля;Олександр Довженко;1930;75;7.9");

        assertInstanceOf(FeatureFilm.class, movie);
        assertSame(MovieKind.FEATURE, movie.getKind());
    }

    @Test
    void fromCsvWithKindCreatesMatchingSubtype() {
        Movie documentary = Movie.fromCsv("Хроніка;Автор;2015;60;8.0;documentary");
        Movie feature = Movie.fromCsv("Кіборги;Ахтем Сеітаблаєв;2017;104;6.8; Feature ");

        assertInstanceOf(Documentary.class, documentary);
        assertInstanceOf(FeatureFilm.class, feature);
    }

    @Test
    void fromCsvRejectsUnknownAndEmptyKind() {
        IllegalArgumentException unknown = assertThrows(IllegalArgumentException.class,
                () -> Movie.fromCsv("А;Б;2000;90;7.0;cartoon"));
        assertEquals("невідомий вид фільму: cartoon", unknown.getMessage());
        assertThrows(IllegalArgumentException.class, () -> Movie.fromCsv("А;Б;2000;90;7.0;"));
    }

    @Test
    void fromCsvRejectsSevenFields() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Movie.fromCsv("А;Б;2000;90;7.0;feature;зайве"));

        assertEquals("очікується 5 полів, отримано 7", error.getMessage());
    }

    // ---------- toString ----------

    @Test
    void toStringUsesDotAsDecimalSeparatorAndKeepsUkrainianText() {
        Movie movie = new FeatureFilm("Земля", "Олександр Довженко", 1930, 75, 7.9);

        assertEquals("Земля (Олександр Довженко, 1930): 75 хв, рейтинг 7.90", movie.toString());
    }
}
