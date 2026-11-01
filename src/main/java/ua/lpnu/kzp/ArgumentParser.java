package ua.lpnu.kzp;

import java.nio.file.Path;

/** Розбирає аргументи командного рядка в {@link CliOptions}. */
public final class ArgumentParser {

    /** Вхідний файл за замовчуванням. */
    public static final String DEFAULT_INPUT = "data/input.csv";

    /** Файл звіту за замовчуванням. */
    public static final String DEFAULT_OUTPUT = "out/report.txt";

    private ArgumentParser() {
    }

    /**
     * Розбирає аргументи. Підтримуються {@code --help}, {@code --version},
     * {@code --input <файл>} і {@code --output <файл>}.
     *
     * @param args аргументи командного рядка
     * @return розібрані параметри
     * @throws IllegalArgumentException якщо параметр невідомий або не має значення
     */
    public static CliOptions parse(String[] args) {
        boolean help = false;
        boolean version = false;
        Path input = Path.of(DEFAULT_INPUT);
        Path output = Path.of(DEFAULT_OUTPUT);

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--help" -> help = true;
                case "--version" -> version = true;
                case "--input" -> {
                    i++;
                    input = Path.of(requireValue(args, i, arg));
                }
                case "--output" -> {
                    i++;
                    output = Path.of(requireValue(args, i, arg));
                }
                default -> throw new IllegalArgumentException("невідомий параметр " + arg);
            }
        }
        return new CliOptions(help, version, input, output);
    }

    private static String requireValue(String[] args, int index, String name) {
        if (index >= args.length) {
            throw new IllegalArgumentException("для " + name + " потрібно вказати значення");
        }
        return args[index];
    }
}
