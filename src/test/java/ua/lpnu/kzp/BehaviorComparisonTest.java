package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Доводить, що перебудова на класи не змінила зовнішню поведінку:
 * звіт для контрольного файла data/input.csv збігається з виводом
 * лабораторної роботи № 1 символ у символ.
 */
class BehaviorComparisonTest {

    @Test
    void reportForControlInputMatchesLab1Output() throws IOException {
        List<String> lines = FileReport.readLines(Path.of("data", "input.csv"));

        String report = MovieCatalogProcessor.formatReport(
                MovieCatalogProcessor.processLines(lines));

        String expectedLab1Output = """
        Показник                       Значення
        Коректних записів                     5
        Середній рейтинг                   7.32
        Найдовший фільм: Плем'я (132 хв)
        Найстаріший фільм: Земля (1930 рік)

        Помилок: 3
        Рядок 3: очікується 5 полів, отримано 4
        Рядок 5: числове поле має помилковий формат
        Рядок 7: від'ємне числове значення
        """;
        // Розділювач рядків залежить від ОС, тому нормалізуємо його.
        assertEquals(expectedLab1Output, report.replace("\r\n", "\n"));
    }
}
