package ua.lpnu.kzp;

/**
 * Документальний фільм.
 *
 * <p>Рекомендаційний бал: рейтинг, помножений на вагу, плюс бонус за кожну
 * хвилину тривалості, але не більше ніж за {@value #BONUS_CAP_MINUTES} хвилин.</p>
 */
public final class Documentary extends Movie {

    /** Найбільша тривалість у хвилинах, за яку нараховується бонус. */
    public static final int BONUS_CAP_MINUTES = 90;

    /** Бонус за кожну хвилину тривалості в межах {@value #BONUS_CAP_MINUTES}. */
    public static final double BONUS_PER_MINUTE = 0.1;

    /**
     * Створює документальний фільм; спільні поля перевіряє базовий клас.
     *
     * @param title    назва
     * @param director режисер
     * @param year     рік випуску
     * @param minutes  тривалість у хвилинах
     * @param rating   рейтинг від 0 до 10
     * @throws IllegalArgumentException якщо значення недопустимі
     */
    public Documentary(String title, String director, int year, int minutes, double rating) {
        super(title, director, year, minutes, rating, MovieKind.DOCUMENTARY);
    }

    /** Обчислює бал документального фільму: тривалість дає обмежений бонус. */
    @Override
    public double recommendationScore() {
        int bonusMinutes = Math.min(getMinutes(), BONUS_CAP_MINUTES);
        return getRating() * RATING_WEIGHT + bonusMinutes * BONUS_PER_MINUTE;
    }
}
