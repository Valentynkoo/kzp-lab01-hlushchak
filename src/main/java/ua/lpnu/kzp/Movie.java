package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Спільний абстрактний тип запису каталогу фільмів (варіант 8).
 *
 * <p>Базовий клас містить усі спільні поля й інваріанти, а відмінну
 * поведінку ({@link #recommendationScore()}) реалізують підтипи
 * {@link FeatureFilm} і {@link Documentary}. Стан приховано в
 * {@code private final}-полях, сеттерів немає, тому некоректний або змінений
 * об'єкт не може потрапити до списку чи статистики.</p>
 *
 * <p><b>Рівність.</b> Два фільми рівні, якщо вони належать одному класу й мають
 * однакові назву, режисера та рік (природний ключ фільму). Тривалість і рейтинг
 * ключем не є. Усі ці поля незмінні, тому об'єкт безпечний як ключ
 * {@code HashMap} і елемент {@code HashSet}.</p>
 */
public abstract class Movie {

    /** Вага рейтингу в рекомендаційному балі, спільна для всіх підтипів. */
    protected static final double RATING_WEIGHT = 10.0;

    private static final int FIELD_COUNT = 5;
    private static final int FIELD_COUNT_WITH_KIND = 6;

    private final String title;
    private final String director;
    private final int minutes;
    private final YearRating yearRating;
    private final MovieKind kind;

    /**
     * Створює коректний запис про фільм. Конструктор доступний лише підтипам.
     *
     * @param title    назва фільму (не null і не порожня)
     * @param director режисер (не null і не порожній)
     * @param year     рік випуску (більший за нуль)
     * @param minutes  тривалість у хвилинах (більша за нуль)
     * @param rating   рейтинг від 0 до 10
     * @param kind     вид фільму (не null)
     * @throws NullPointerException     якщо назва, режисер або вид дорівнюють null
     * @throws IllegalArgumentException якщо будь-яке значення недопустиме
     */
    protected Movie(String title, String director, int year, int minutes, double rating,
                    MovieKind kind) {
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
        this.kind = Objects.requireNonNull(kind, "Вид фільму не може бути null");
    }

    /**
     * Створює запис із CSV-рядка {@code title;director;year;minutes;rating[;kind]}.
     *
     * <p>Шосте поле необов'язкове: код виду ({@code feature} або
     * {@code documentary}). Без нього фільм вважається художнім, тому файли
     * попередніх робіт читаються без змін. Фабрика відповідає за структуру рядка й
     * типи полів, а конструктор — за інваріанти. Підтип обирає сам вид через
     * {@link MovieKind#create}, без розгалуження в цьому методі.</p>
     *
     * @param line рядок із п'ятьма або шістьма полями, розділеними крапкою з комою
     * @return створений запис відповідного підтипу
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
        // Текст помилки лишився як у лабораторній № 1, щоб звіт не змінився.
        if (fields.length != FIELD_COUNT && fields.length != FIELD_COUNT_WITH_KIND) {
            throw new IllegalArgumentException(
                    "очікується %d полів, отримано %d".formatted(FIELD_COUNT, fields.length));
        }
        int year;
        int minutes;
        double rating;
        try {
            year = Integer.parseInt(fields[2].trim());
            minutes = Integer.parseInt(fields[3].trim());
            rating = Double.parseDouble(fields[4].trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("числове поле має помилковий формат", exception);
        }
        MovieKind kind = fields.length == FIELD_COUNT_WITH_KIND
                ? MovieKind.fromCode(fields[5])
                : MovieKind.FEATURE;
        return kind.create(fields[0].trim(), fields[1].trim(), year, minutes, rating);
    }

    /**
     * Обчислює рекомендаційний бал фільму. Формулу задає кожен підтип,
     * тому код звітності не перевіряє, який саме підтип отримано.
     *
     * @return рекомендаційний бал (більший бал означає кращу рекомендацію)
     */
    public abstract double recommendationScore();

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

    /** Повертає вид фільму. */
    public MovieKind getKind() {
        return kind;
    }

    /**
     * Порівнює фільми за класом, назвою, режисером і роком.
     *
     * <p>{@code getClass()} не допускає рівності художнього й документального
     * фільму з однаковим ключем. Метод {@code final}, щоб підтип не міг
     * порушити контракт разом із {@link #hashCode()}.</p>
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Movie movie = (Movie) other;
        return title.equals(movie.title)
                && director.equals(movie.director)
                && yearRating.year() == movie.yearRating.year();
    }

    /** Повертає хеш-код, узгоджений із {@link #equals(Object)}: ті самі поля. */
    @Override
    public final int hashCode() {
        return Objects.hash(getClass().getName(), title, director, yearRating.year());
    }

    /** Повертає текстове подання запису; дробове число форматується з крапкою. */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s (%s, %d): %d хв, рейтинг %.2f",
                title, director, yearRating.year(), minutes, yearRating.rating());
    }
}
