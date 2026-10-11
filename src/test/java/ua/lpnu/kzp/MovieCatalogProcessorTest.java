package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Перевіряє парсинг, валідацію та показники варіанта 8. */
class MovieCatalogProcessorTest {

    private static final double DELTA = 0.0001;

    @Test
    void validRecordsGiveCorrectIndicators() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Земля;Олександр Довженко;1930;75;7.9",
                "Носоріг;Олег Сенцов;2021;107;6.5"));

        assertEquals(2, r.validCount());
        assertEquals(7.2, r.averageRating(), DELTA);
        assertEquals("Носоріг", r.longestFilmTitle());
        assertEquals(107, r.longestFilmMinutes());
        assertEquals("Земля", r.oldestFilmTitle());
        assertEquals(1930, r.oldestYear());
        assertTrue(r.errors().isEmpty());
    }

    @Test
    void averageIsNotTruncatedToInteger() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "А;Б;2000;90;7.0",
                "В;Г;2001;90;8.0",
                "Д;Е;2002;90;8.0"));

        assertEquals(23.0 / 3, r.averageRating(), DELTA);
    }

    @Test
    void blankLineIsReportedWithLineNumber() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Земля;Олександр Довженко;1930;75;7.9",
                "   "));

        assertEquals(1, r.validCount());
        assertEquals(1, r.errors().size());
        assertTrue(r.errors().get(0).startsWith("Рядок 2"));
    }

    @Test
    void wrongFieldCountIsSkipped() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Позивний Бандерас;Богдан Слюсаревський;2018;99"));

        assertEquals(0, r.validCount());
        assertEquals(1, r.errors().size());
        assertTrue(r.errors().get(0).contains("очікується 5 полів"));
    }

    @Test
    void emptyTrailingFieldIsNotSilentlyDropped() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Земля;Олександр Довженко;1930;75;"));

        assertEquals(0, r.validCount());
        assertTrue(r.errors().get(0).contains("помилковий формат"));
    }

    @Test
    void nonNumericFieldIsSkipped() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Земля кличе;Іван Кравченко;19XX;95;7.0"));

        assertEquals(0, r.validCount());
        assertTrue(r.errors().get(0).contains("помилковий формат"));
    }

    @Test
    void negativeValueIsSkipped() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Мій Карабах;Тарас Ткаченко;2020;-45;5.5"));

        assertEquals(0, r.validCount());
        assertTrue(r.errors().get(0).contains("від'ємне"));
    }

    @Test
    void emptyTitleAndEmptyDirectorAreSkipped() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                ";Режисер;2000;90;7.0",
                "Назва; ;2000;90;7.0"));

        assertEquals(0, r.validCount());
        assertEquals(2, r.errors().size());
    }

    @Test
    void invalidRowDoesNotChangeIndicators() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Земля;Олександр Довженко;1930;75;7.9",
                "Довгий;Хтось;1800;500;10.0;зайве",
                "Плем'я;Мирослав Слабошпицький;2014;132;7.3"));

        assertEquals(2, r.validCount());
        assertEquals(7.6, r.averageRating(), DELTA);
        assertEquals(132, r.longestFilmMinutes());
        assertEquals(1930, r.oldestYear());
    }

    @Test
    void fileWithoutValidRecordsIsHandled() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of("", "сміття"));

        assertEquals(0, r.validCount());
        assertEquals(0.0, r.averageRating(), DELTA);
        assertNull(r.longestFilmTitle());
        assertNull(r.oldestFilmTitle());
        assertTrue(MovieCatalogProcessor.formatReport(r).contains("немає коректних даних"));
    }

    @Test
    void ukrainianTextIsPreservedInReport() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Тіні забутих предків;Сергій Параджанов;1965;97;8.1"));

        String report = MovieCatalogProcessor.formatReport(r);
        assertTrue(report.contains("Тіні забутих предків"));
    }

    @Test
    void reportUsesDotAsDecimalSeparator() {
        ProcessingResult r = MovieCatalogProcessor.processLines(List.of(
                "Земля;Олександр Довженко;1930;75;7.9",
                "Носоріг;Олег Сенцов;2021;107;6.5"));

        String report = MovieCatalogProcessor.formatReport(r);
        assertTrue(report.contains("7.20"));
        assertTrue(!report.contains("7,20"));
    }
}
