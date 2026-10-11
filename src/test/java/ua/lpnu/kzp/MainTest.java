package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Наскрізні перевірки командного рядка. */
class MainTest {

    @Test
    void helpAndVersionDoNotThrow() {
        assertDoesNotThrow(() -> Main.main(new String[] {"--help"}));
        assertDoesNotThrow(() -> Main.main(new String[] {"--version"}));
    }

    @Test
    void endToEndWritesReportFile(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("out").resolve("report.txt");
        Files.writeString(input,
                "Земля;Олександр Довженко;1930;75;7.9\nнеправильний рядок\n",
                StandardCharsets.UTF_8);

        Main.main(new String[] {"--input", input.toString(), "--output", output.toString()});

        String report = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(report.contains("Земля"));
        assertTrue(report.contains("Рядок 2"));
    }

    @Test
    void missingInputFileDoesNotCrash(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("немає.csv");
        assertDoesNotThrow(() -> Main.main(new String[] {"--input", missing.toString()}));
    }
}
