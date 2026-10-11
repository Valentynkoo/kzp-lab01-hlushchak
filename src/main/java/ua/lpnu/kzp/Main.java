package ua.lpnu.kzp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

/**
 * Точка входу консольної програми «Каталог фільмів» (варіант 8).
 *
 * <p>Програма читає CSV із записами про фільми
 * ({@code title;director;year;minutes;rating}), перевіряє їх,
 * обчислює чотири показники (кількість коректних записів, середній
 * рейтинг, найдовший фільм, найстаріший рік) і виводить звіт у
 * консоль та у файл.</p>
 */
public final class Main {

    private static final String DEFAULT_INPUT = "data/input.csv";
    private static final String DEFAULT_OUTPUT = "out/report.txt";

    private Main() {
    }

    /**
     * Головний метод програми.
     *
     * <p>Режими: {@code --help} (довідка), звичайний запуск та
     * {@code --version}. Шляхи задаються через {@code --input <файл>}
     * і {@code --output <файл>}.</p>
     *
     * @param args аргументи командного рядка
     * @throws IOException якщо не вдалося записати звіт або прочитати версію
     */
    public static void main(String[] args) throws IOException {
        if (containsFlag(args, "--help")) {
            printUsage();
            return;
        }
        if (containsFlag(args, "--version")) {
            System.out.printf(Locale.ROOT, "lab01 версія %s%n", readVersion());
            return;
        }

        Path input = Path.of(getOption(args, "--input").orElse(DEFAULT_INPUT));
        Path output = Path.of(getOption(args, "--output").orElse(DEFAULT_OUTPUT));

        List<String> lines;
        try {
            lines = FileReport.readLines(input);
        } catch (IOException exception) {
            // Хибний шлях не повинен завершувати програму без пояснення.
            System.out.printf(Locale.ROOT,
                    "Не вдалося прочитати файл %s: файл не знайдено або недоступний%n", input);
            return;
        }

        ProcessingResult result = MovieCatalogProcessor.processLines(lines);
        String report = MovieCatalogProcessor.formatReport(result);

        System.out.print(report);
        FileReport.writeReport(output, report);
    }

    private static void printUsage() {
        System.out.printf(Locale.ROOT,
                "Використання: java -jar lab01-1.0.0.jar [--help] [--version] "
                        + "[--input <файл>] [--output <файл>]%n"
                        + "  --help           показати довідку%n"
                        + "  --version        показати версію програми%n"
                        + "  --input <файл>   вхідний CSV (за замовчуванням %s)%n"
                        + "  --output <файл>  файл звіту (за замовчуванням %s)%n",
                DEFAULT_INPUT, DEFAULT_OUTPUT);
    }

    private static boolean containsFlag(String[] args, String flag) {
        for (String arg : args) {
            if (flag.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static Optional<String> getOption(String[] args, String name) {
        for (int i = 0; i < args.length - 1; i++) {
            if (name.equals(args[i])) {
                return Optional.of(args[i + 1]);
            }
        }
        return Optional.empty();
    }

    private static String readVersion() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = Main.class.getResourceAsStream("/version.properties")) {
            if (in == null) {
                return "unknown";
            }
            properties.load(in);
        }
        return properties.getProperty("version", "unknown");
    }
}
