package ua.lpnu.kzp;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Точка входу програми «Каталог фільмів» (варіант 8).
 *
 * <p>Клас лише запускає процес: розбір параметрів, контролер і код
 * завершення. Уся обробка даних виконується в інших класах.</p>
 */
public final class Main {

    private Main() {
    }

    /**
     * Головний метод програми.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        // Явне UTF-8, бо кодування консолі Windows за замовчуванням може не мати українських літер.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out),
                true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err),
                true, StandardCharsets.UTF_8));

        int exitCode = run(args);
        if (exitCode != ApplicationController.EXIT_OK) {
            System.exit(exitCode);
        }
    }

    /**
     * Виконує програму й повертає код завершення (без завершення JVM,
     * тому метод можна викликати з тестів).
     *
     * @param args аргументи командного рядка
     * @return код завершення
     */
    static int run(String[] args) {
        CliOptions options;
        try {
            options = ArgumentParser.parse(args);
        } catch (IllegalArgumentException exception) {
            System.err.printf(Locale.ROOT, "Помилка: %s. Скористайтесь --help.%n",
                    exception.getMessage());
            return ApplicationController.EXIT_BAD_ARGUMENTS;
        }
        return new ApplicationController().execute(options);
    }
}
