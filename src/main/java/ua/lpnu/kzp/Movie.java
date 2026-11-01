package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Незмінний запис каталогу фільмів (варіант 8).
 *
 * <p>Стан приховано в {@code private final}-полях, сеттерів немає.
 * Усі інваріанти перевіряються в конструкторі, тому некоректний
 * об'єкт не може потрапити до списку чи статистики.</p>
 */
public final class Movie {

    private static final int FIELD_COUNT = 5;

    private final String title;
    private final String director;
    private final int minutes;
    private final YearRating yearRating;

    /**
     * Створює коректний запис про фільм.
     *
     * @param title    назва фільму (не null і не порожня)
     * @param director режисер (не null і не порожній)
     * @param year     рік випуску (більший за нуль)
     * @param minutes  тривалість у хвилинах (більша за нуль)
     * @param rating   рейтинг від 0 до 10
     * @throws NullPointerException     якщо назва або режисер дорівнюють null
     * @throws IllegalArgumentException якщо будь-яке значення недопустиме
     */
    public Movie(String title, String director, int year, int minutes, double rating) {
        this.title = Objects.requireNonNull(title, "Назва не може бути null");
        if (title.isBlank()) {
            throw new IllegalArgumentException("порожня назва фільму");
        }
        this.director = Objects.requireNonNull(director, "Режисер не може бути null");
        if (director.isBlank()) {
            throw new IllegalArgumentException("порожнє ім'я режисера");
        }
        // Рік і рейтинг перевіряє сам record YearRating.
        this.yearRating = new YearRating(year, rating);
        if (minutes < 0) {
            throw new IllegalArgumentException("від'ємне числове значення");
        }
        if (minutes == 0) {
            throw new IllegalArgumentException("тривалість має бути додатною");
        }
        this.minutes = minutes;
    }

    /**
     * Створює запис із CSV-рядка {@code title;director;year;minutes;rating}.
     *
     * <p>Фабрика відповідає за структуру рядка й типи полів, а конструктор —
     * за інваріанти. Тому обійти перевірки іншим шляхом не вийде.</p>
     *
     * @param line рядок із п'ятьма полями, розділеними крапкою з комою
     * @return створений запис
     * @throws NullPointerException     якщо рядок дорівнює null
     * @throws IllegalArgumentException якщо структура, число або значення некоректні
     */
    public static Movie fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        if (line.isBlank()) {
            throw new IllegalArgumentException("порожній рядок");
        }
        // -1 зберігає порожнє останнє поле, тому воно не губиться мовчки.
        String[] fields = line.split(";", -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "очікується %d полів, отримано %d".formatted(FIELD_COUNT, fields.length));
        }
        try {
            int year = Integer.parseInt(fields[2].trim());
            int minutes = Integer.parseInt(fields[3].trim());
            double rating = Double.parseDouble(fields[4].trim());
            return new Movie(fields[0].trim(), fields[1].trim(), year, minutes, rating);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("числове поле має помилковий формат", exception);
        }
    }

    /** Повертає назву фільму. */
    public String getTitle() {
        return title;
    }

    /** Повертає режисера. */
    public String getDirector() {
        return director;
    }

    /** Повертає рік випуску. */
    public int getYear() {
        return yearRating.year();
    }

    /** Повертає тривалість у хвилинах. */
    public int getMinutes() {
        return minutes;
    }

    /** Повертає рейтинг. */
    public double getRating() {
        return yearRating.rating();
    }

    /** Повертає текстове подання запису; дробове число форматується з крапкою. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s (%s, %d): %d хв, рейтинг %.2f",
                title, director, yearRating.year(), minutes, yearRating.rating());
    }
}
