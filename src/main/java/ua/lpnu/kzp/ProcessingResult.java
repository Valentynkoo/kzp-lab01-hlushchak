package ua.lpnu.kzp;

import java.util.List;

/**
 * Агрегований результат обробки каталогу фільмів (варіант 8).
 *
 * <p>Це лише носій готових даних: обчислення виконує
 * {@link MovieCatalogProcessor}. Клас навмисно не моделює один запис
 * «фільм» — за методичкою окремий рядок CSV поки залишається масивом
 * полів, а власні класи з'являться в лабораторній роботі № 2.</p>
 *
 * @param validCount         кількість коректних записів (показник 1)
 * @param averageRating      середній рейтинг коректних записів (показник 2)
 * @param longestFilmTitle   назва найдовшого фільму або {@code null}
 * @param longestFilmMinutes тривалість найдовшого фільму, хв (показник 3)
 * @param oldestFilmTitle    назва найстарішого фільму або {@code null}
 * @param oldestYear         найменший рік випуску (показник 4)
 * @param errors             повідомлення про пропущені рядки (номер і причина)
 */
public record ProcessingResult(
        int validCount,
        double averageRating,
        String longestFilmTitle,
        int longestFilmMinutes,
        String oldestFilmTitle,
        int oldestYear,
        List<String> errors) {

    /** Робить незмінну копію списку помилок, щоб запис не ділив змінний стан. */
    public ProcessingResult {
        errors = List.copyOf(errors);
    }
}
