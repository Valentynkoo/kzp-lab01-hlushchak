package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Перевіряє каталог із фільмами обох підтипів: поліморфний бал, види, дублікати
 * (HashSet) і хибний вид у CSV.
 */
class MixedCatalogTest {

    @Test
    void mixedCatalogProducesExpectedReportWithRecommendations() throws IOException {
        List<String> lines = FileReport.readLines(Path.of("data", "input-mixed.csv"));

        ProcessingResult result = MovieCatalogProcessor.processLines(lines);
        String report = MovieCatalogProcessor.formatReport(result)
                + MovieCatalogProcessor.formatRecommendations(result);

        String expected = """
        Показник                       Значення
        Коректних записів                     7
        Середній рейтинг                   7.90
        Найдовший фільм: Довга сага (250 хв)
        Найстаріший фільм: Земля (1930 рік)

        Помилок: 1
        Рядок 5: невідомий вид фільму: cartoon

        Рекомендації
        Найкращий за рекомендаційним балом: Хроніка нескорених (88.50)
        Фільмів за видом: художній: 5, документальний: 2
        Унікальних фільмів: 6
        """;
        assertEquals(expected, report.replace("\r\n", "\n"));
    }

    @Test
    void duplicateDocumentaryIsCountedOnceAsUnique() throws IOException {
        List<String> lines = FileReport.readLines(Path.of("data", "input-mixed.csv"));

        ProcessingResult result = MovieCatalogProcessor.processLines(lines);

        // 7 коректних записів, але два з них — один і той самий документальний фільм.
        assertEquals(7, result.movies().size());
        assertTrue(MovieCatalogProcessor.formatRecommendations(result)
                .contains("Унікальних фільмів: 6"));
    }

    @Test
    void emptyInputGivesNoRecommendationData() {
        ProcessingResult result = MovieCatalogProcessor.processLines(List.of());

        String block = MovieCatalogProcessor.formatRecommendations(result).replace("\r\n", "\n");

        assertEquals("""

        Рекомендації
        Найкращий за рекомендаційним балом: немає коректних даних
        Фільмів за видом: художній: 0, документальний: 0
        Унікальних фільмів: 0
        """, block);
    }

    @Test
    void invalidRowsDoNotAffectRecommendations() {
        ProcessingResult result = MovieCatalogProcessor.processLines(List.of(
                "Хроніка;Автор;2015;60;8.0;documentary",
                "Зламаний рядок;Автор;19XX;90;7.0",
                "Невідомий;Автор;2000;90;7.0;cartoon"));

        assertEquals(1, result.movies().size());
        assertEquals(2, result.errors().size());
    }
}
