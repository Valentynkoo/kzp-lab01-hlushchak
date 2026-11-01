package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Наскрізні перевірки командного рядка.
 *
 * <p>Порівняно з лабораторною роботою № 1 виклики {@code Main.main} замінено
 * на {@code Main.run}: {@code main} тепер налаштовує консоль і завершує JVM
 * з кодом завершення, тому для тестів потрібен метод, що лише повертає код.
 * Самі перевірки збережено, додано перевірку кодів.</p>
 */
class MainTest {

    @Test
    void helpAndVersionReturnOk() {
        assertEquals(0, Main.run(new String[] {"--help"}));
        assertEquals(0, Main.run(new String[] {"--version"}));
    }

    @Test
    void endToEndWritesReportFile(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("out").resolve("report.txt");
        Files.writeString(input,
                "Земля;Олександр Довженко;1930;75;7.9\nнеправильний рядок\n",
                StandardCharsets.UTF_8);

        int code = Main.run(new String[] {"--input", input.toString(), "--output", output.toString()});

        assertEquals(0, code);
        String report = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(report.contains("Земля"));
        assertTrue(report.contains("Рядок 2"));
    }

    @Test
    void missingInputFileReturnsIoErrorCode(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("missing.csv");

        assertEquals(2, Main.run(new String[] {"--input", missing.toString()}));
    }

    @Test
    void unknownOptionReturnsBadArgumentsCode() {
        assertEquals(1, Main.run(new String[] {"--bogus"}));
    }
}
