package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Обробляє записи каталогу фільмів (варіант 8).
 *
 * <p>Розбір і перевірку одного рядка виконує {@link Movie#fromCsv(String)},
 * а цей клас збирає список об'єктів, обчислює показники та формує звіт.
 * Хибний рядок не змінює показники, а лише додає повідомлення до списку
 * помилок.</p>
 */
public final class MovieCatalogProcessor {

    private MovieCatalogProcessor() {
    }

    /**
     * Обробляє всі рядки файла й обчислює чотири показники варіанта.
     *
     * <p>Кожен рядок перетворюється на об'єкт {@link Movie} через
     * {@link Movie#fromCsv(String)}. Хибний рядок додає повідомлення з
     * номером рядка до списку помилок і не потрапляє в обчислення.</p>
     *
     * @param lines рядки вхідного файла
     * @return агрегований результат разом зі списком помилок
     */
    public static ProcessingResult processLines(List<String> lines) {
        List<Movie> movies = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            try {
                movies.add(Movie.fromCsv(lines.get(index)));
            } catch (IllegalArgumentException exception) {
                errors.add("Рядок %d: %s".formatted(index + 1, exception.getMessage()));
            }
        }
        return summarize(movies, errors);
    }

    /** Обчислює показники за списком об'єктів, а не за полями з індексами. */
    private static ProcessingResult summarize(List<Movie> movies, List<String> errors) {
        double ratingSum = 0.0;
        Movie longest = null;
        Movie oldest = null;

        for (Movie movie : movies) {
            ratingSum += movie.getRating();
            // Суворе порівняння: за рівних значень лишається перший запис.
            if (longest == null || movie.getMinutes() > longest.getMinutes()) {
                longest = movie;
            }
            if (oldest == null || movie.getYear() < oldest.getYear()) {
                oldest = movie;
            }
        }

        int count = movies.size();
        // Порожній набір обробляємо окремо, щоб не ділити на нуль.
        double averageRating = count == 0 ? 0.0 : ratingSum / count;

        return new ProcessingResult(
                count,
                averageRating,
                longest == null ? null : longest.getTitle(),
                longest == null ? 0 : longest.getMinutes(),
                oldest == null ? null : oldest.getTitle(),
                oldest == null ? 0 : oldest.getYear(),
                errors,
                movies);
    }

    /**
     * Знаходить фільм із найбільшим рекомендаційним балом.
     *
     * <p>Метод працює зі спільним типом {@link Movie} і викликає
     * {@link Movie#recommendationScore()}: формулу вибирає фактичний підтип.
     * За рівних балів лишається перший фільм зі списку.</p>
     *
     * @param movies фільми для порівняння
     * @return найкращий фільм або порожній {@code Optional}, якщо список порожній
     */
    public static Optional<Movie> bestRecommended(List<Movie> movies) {
        Movie best = null;
        for (Movie movie : movies) {
            if (best == null || movie.recommendationScore() > best.recommendationScore()) {
                best = movie;
            }
        }
        return Optional.ofNullable(best);
    }

    /**
     * Рахує фільми за видом; усі види присутні в результаті, навіть із нулем.
     *
     * @param movies фільми для підрахунку
     * @return кількість фільмів за видом у порядку оголошення enum
     */
    public static Map<MovieKind, Integer> countByKind(List<Movie> movies) {
        Map<MovieKind, Integer> counts = new EnumMap<>(MovieKind.class);
        for (MovieKind kind : MovieKind.values()) {
            counts.put(kind, 0);
        }
        for (Movie movie : movies) {
            counts.merge(movie.getKind(), 1, Integer::sum);
        }
        return counts;
    }

    /**
     * Формує додатковий блок «Рекомендації». Основний звіт
     * {@link #formatReport(ProcessingResult)} не змінюється, а цей блок
     * дописується після нього.
     *
     * @param result результат {@link #processLines(List)}
     * @return текст блоку, що починається з порожнього рядка
     */
    public static String formatRecommendations(ProcessingResult result) {
        StringBuilder block = new StringBuilder();
        block.append(String.format(Locale.ROOT, "%nРекомендації%n"));

        Optional<Movie> best = bestRecommended(result.movies());
        if (best.isEmpty()) {
            block.append(String.format(Locale.ROOT,
                    "Найкращий за рекомендаційним балом: немає коректних даних%n"));
        } else {
            block.append(String.format(Locale.ROOT,
                    "Найкращий за рекомендаційним балом: %s (%.2f)%n",
                    best.get().getTitle(), best.get().recommendationScore()));
        }

        StringBuilder kinds = new StringBuilder();
        for (Map.Entry<MovieKind, Integer> entry : countByKind(result.movies()).entrySet()) {
            if (kinds.length() > 0) {
                kinds.append(", ");
            }
            kinds.append(entry.getKey().label()).append(": ").append(entry.getValue());
        }
        block.append(String.format(Locale.ROOT, "Фільмів за видом: %s%n", kinds));
        // HashSet скористається узгодженими equals і hashCode: дублікати не рахуються.
        block.append(String.format(Locale.ROOT, "Унікальних фільмів: %d%n",
                new HashSet<>(result.movies()).size()));
        return block.toString();
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
