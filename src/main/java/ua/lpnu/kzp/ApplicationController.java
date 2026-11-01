package ua.lpnu.kzp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Керує роботою програми: показує довідку чи версію або читає файл,
 * обробляє записи, виводить звіт у консоль і записує його у файл.
 *
 * <p>Метод {@link #execute(CliOptions)} повертає код завершення, а
 * {@link Main} лише передає його операційній системі.</p>
 */
public final class ApplicationController {

    /** Успішне завершення. */
    public static final int EXIT_OK = 0;

    /** Некоректні аргументи командного рядка. */
    public static final int EXIT_BAD_ARGUMENTS = 1;

    /** Помилка читання вхідного файла або запису звіту. */
    public static final int EXIT_IO_ERROR = 2;

    /**
     * Виконує дію, обрану параметрами.
     *
     * @param options розібрані параметри командного рядка
     * @return код завершення: 0, або 2 за помилки роботи з файлами
     */
    public int execute(CliOptions options) {
        if (options.help()) {
            printUsage();
            return EXIT_OK;
        }
        if (options.version()) {
            System.out.printf(Locale.ROOT, "lab01 версія %s%n", readVersion());
            return EXIT_OK;
        }

        List<String> lines;
        try {
            lines = FileReport.readLines(options.input());
        } catch (IOException exception) {
            System.err.printf(Locale.ROOT, "Не вдалося прочитати файл %s: %s%n",
                    options.input(), exception.getClass().getSimpleName());
            return EXIT_IO_ERROR;
        }

        ProcessingResult result = MovieCatalogProcessor.processLines(lines);
        String report = MovieCatalogProcessor.formatReport(result);
        System.out.print(report);

        try {
            FileReport.writeReport(options.output(), report);
        } catch (IOException exception) {
            System.err.printf(Locale.ROOT, "Не вдалося записати звіт %s: %s%n",
                    options.output(), exception.getClass().getSimpleName());
            return EXIT_IO_ERROR;
        }
        return EXIT_OK;
    }

    private static void printUsage() {
        // Назва jar містить версію з pom.xml, тому в коді номера версії немає.
        System.out.printf(Locale.ROOT,
                "Використання: java -jar lab01-%s.jar [--help] [--version] "
                        + "[--input <файл>] [--output <файл>]%n"
                        + "  --help           показати довідку%n"
                        + "  --version        показати версію програми%n"
                        + "  --input <файл>   вхідний CSV (за замовчуванням %s)%n"
                        + "  --output <файл>  файл звіту (за замовчуванням %s)%n"
                        + "Коди завершення: 0 — успіх, 1 — хибні аргументи, 2 — помилка файла%n",
                readVersion(), ArgumentParser.DEFAULT_INPUT, ArgumentParser.DEFAULT_OUTPUT);
    }

    /** Читає версію, яку Maven підставив у version.properties із pom.xml. */
    private static String readVersion() {
        Properties properties = new Properties();
        try (InputStream in = ApplicationController.class.getResourceAsStream("/version.properties")) {
            if (in == null) {
                return "unknown";
            }
            properties.load(in);
        } catch (IOException exception) {
            return "unknown";
        }
        return properties.getProperty("version", "unknown");
    }
}
