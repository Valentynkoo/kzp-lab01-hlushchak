package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Обробляє записи каталогу фільмів (варіант 8).
 *
 * <p>Формат одного запису: {@code title;director;year;minutes;rating}
 * (роздільник — крапка з комою, {@code rating} — число з крапкою).</p>
 *
 * <p>Порядок обробки кожного рядка: прочитати → перевірити →
 * перетворити → включити в обчислення. Хибний рядок не змінює
 * показники, а лише додає повідомлення до списку помилок.</p>
 */
public final class MovieCatalogProcessor {

    private static final int FIELD_COUNT = 5;

    private MovieCatalogProcessor() {
    }

    /**
     * Обробляє всі рядки файла й обчислює чотири показники варіанта.
     *
     * @param lines рядки вхідного файла
     * @return агрегований результат разом зі списком помилок
     */
    public static ProcessingResult processLines(List<String> lines) {
        List<String> errors = new ArrayList<>();

        int validCount = 0;
        double ratingSum = 0.0;

        String longestFilmTitle = null;
        int longestFilmMinutes = Integer.MIN_VALUE;

        String oldestFilmTitle = null;
        int oldestYear = Integer.MAX_VALUE;

        for (int index = 0; index < lines.size(); index++) {
            int lineNumber = index + 1;
            String line = lines.get(index);

            if (line.isBlank()) {
                errors.add("Рядок %d: порожній рядок".formatted(lineNumber));
                continue;
            }

            // -1 зберігає порожнє останнє поле, якщо воно є.
            String[] fields = line.split(";", -1);
            if (fields.length != FIELD_COUNT) {
                errors.add("Рядок %d: очікується %d полів, отримано %d"
                        .formatted(lineNumber, FIELD_COUNT, fields.length));
                continue;
            }

            String title = fields[0].trim();
            String director = fields[1].trim();

            if (title.isEmpty()) {
                errors.add("Рядок %d: порожня назва фільму".formatted(lineNumber));
                continue;
            }
            if (director.isEmpty()) {
                errors.add("Рядок %d: порожнє ім'я режисера".formatted(lineNumber));
                continue;
            }

            int year;
            int minutes;
            double rating;
            try {
                // Пошкоджене числове поле кидає NumberFormatException.
                year = Integer.parseInt(fields[2].trim());
                minutes = Integer.parseInt(fields[3].trim());
                rating = Double.parseDouble(fields[4].trim());
            } catch (NumberFormatException exception) {
                errors.add("Рядок %d: числове поле має помилковий формат".formatted(lineNumber));
                continue;
            }

            if (year < 0 || minutes < 0 || rating < 0) {
                errors.add("Рядок %d: від'ємне числове значення".formatted(lineNumber));
                continue;
            }

            // Рядок пройшов усі перевірки — включаємо його в статистику.
            validCount++;
            ratingSum += rating;

            if (minutes > longestFilmMinutes) {
                longestFilmMinutes = minutes;
                longestFilmTitle = title;
            }
            if (year < oldestYear) {
                oldestYear = year;
                oldestFilmTitle = title;
            }
        }

        // Порожній набір обробляємо окремо, щоб не ділити на нуль.
        double averageRating = validCount == 0 ? 0.0 : ratingSum / validCount;
        int longestMinutes = validCount == 0 ? 0 : longestFilmMinutes;
        int oldest = validCount == 0 ? 0 : oldestYear;

        return new ProcessingResult(validCount, averageRating, longestFilmTitle,
                longestMinutes, oldestFilmTitle, oldest, errors);
    }

    /**
     * Формує текст звіту. Той самий текст виводять і в консоль, і у файл,
     * тому логіка форматування не дублюється.
     *
     * @param result результат {@link #processLines(List)}
     * @return форматований звіт
     */
    public static String formatReport(ProcessingResult result) {
        StringBuilder report = new StringBuilder();

        report.append(String.format(Locale.ROOT, "%-28s %10s%n", "Показник", "Значення"));
        report.append(String.format(Locale.ROOT, "%-28s %10d%n",
                "Коректних записів", result.validCount()));
        report.append(String.format(Locale.ROOT, "%-28s %10.2f%n",
                "Середній рейтинг", result.averageRating()));

        if (result.validCount() == 0) {
            report.append(String.format(Locale.ROOT,
                    "Найдовший фільм: немає коректних даних%n"));
            report.append(String.format(Locale.ROOT,
                    "Найстаріший фільм: немає коректних даних%n"));
        } else {
            report.append(String.format(Locale.ROOT, "Найдовший фільм: %s (%d хв)%n",
                    result.longestFilmTitle(), result.longestFilmMinutes()));
            report.append(String.format(Locale.ROOT, "Найстаріший фільм: %s (%d рік)%n",
                    result.oldestFilmTitle(), result.oldestYear()));
        }

        report.append(String.format(Locale.ROOT, "%nПомилок: %d%n", result.errors().size()));
        for (String error : result.errors()) {
            report.append(String.format(Locale.ROOT, "%s%n", error));
        }

        return report.toString();
    }
}
