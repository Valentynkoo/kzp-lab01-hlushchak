package ua.lpnu.kzp;

/**
 * Незмінна пара «рік випуску — рейтинг» для фільму.
 *
 * <p>Record автоматично отримує конструктор, методи {@code year()} і
 * {@code rating()}, а також {@code equals}, {@code hashCode} і
 * {@code toString}. Компактний конструктор нижче перевіряє допустимість
 * значень, тому некоректна пара не може існувати.</p>
 *
 * @param year   рік випуску (більший за нуль)
 * @param rating рейтинг у межах від 0 до {@value #MAX_RATING}
 */
public record YearRating(int year, double rating) {

    /** Найбільший допустимий рейтинг. */
    public static final double MAX_RATING = 10.0;

    /** Перевіряє рік і рейтинг до створення запису. */
    public YearRating {
        if (year < 0 || rating < 0) {
            throw new IllegalArgumentException("від'ємне числове значення");
        }
        if (year == 0) {
            throw new IllegalArgumentException("рік має бути додатним");
        }
        // NaN і нескінченність не потрапляють у діапазон, тому відхиляються тут.
        if (!Double.isFinite(rating) || rating > MAX_RATING) {
            throw new IllegalArgumentException("рейтинг має бути в межах від 0 до 10");
        }
    }
}
