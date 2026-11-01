package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Розібрані параметри командного рядка.
 *
 * @param help    чи потрібно показати довідку
 * @param version чи потрібно показати версію
 * @param input   шлях до вхідного CSV
 * @param output  шлях до файла звіту
 */
public record CliOptions(boolean help, boolean version, Path input, Path output) {

    /** Забороняє порожні шляхи. */
    public CliOptions {
        Objects.requireNonNull(input, "Шлях входу не може бути null");
        Objects.requireNonNull(output, "Шлях виходу не може бути null");
    }
}
