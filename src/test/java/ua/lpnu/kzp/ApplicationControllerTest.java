package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Перевіряє керування роботою та коди завершення. */
class ApplicationControllerTest {

    private final ApplicationController controller = new ApplicationController();

    @Test
    void successfulRunWritesReportAndReturnsOk(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("out").resolve("report.txt");
        Files.writeString(input,
                "Земля;Олександр Довженко;1930;75;7.9\nхибний рядок\n", StandardCharsets.UTF_8);

        int code = controller.execute(new CliOptions(false, false, input, output));

        assertEquals(ApplicationController.EXIT_OK, code);
        String report = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(report.contains("Земля"));
        assertTrue(report.contains("Рядок 2"));
    }

    @Test
    void missingInputReturnsIoErrorCode(@TempDir Path tempDir) {
        Path input = tempDir.resolve("missing.csv");
        Path output = tempDir.resolve("report.txt");

        int code = controller.execute(new CliOptions(false, false, input, output));

        assertEquals(ApplicationController.EXIT_IO_ERROR, code);
    }

    @Test
    void unwritableOutputReturnsIoErrorCode(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Files.writeString(input, "Земля;Олександр Довженко;1930;75;7.9\n", StandardCharsets.UTF_8);
        // Звичайний файл не може бути каталогом, тому запис звіту всередину нього неможливий.
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "x", StandardCharsets.UTF_8);
        Path output = blocker.resolve("report.txt");

        int code = controller.execute(new CliOptions(false, false, input, output));

        assertEquals(ApplicationController.EXIT_IO_ERROR, code);
    }

    @Test
    void helpAndVersionReturnOk(@TempDir Path tempDir) {
        Path any = tempDir.resolve("any");

        assertEquals(ApplicationController.EXIT_OK,
                controller.execute(new CliOptions(true, false, any, any)));
        assertEquals(ApplicationController.EXIT_OK,
                controller.execute(new CliOptions(false, true, any, any)));
    }
}
