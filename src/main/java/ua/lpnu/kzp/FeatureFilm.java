package ua.lpnu.kzp;

/**
 * Художній фільм.
 *
 * <p>Рекомендаційний бал: рейтинг, помножений на вагу, мінус штраф за кожну
 * хвилину тривалості понад {@value #COMFORTABLE_MINUTES}.</p>
 */
public final class FeatureFilm extends Movie {

    /** Тривалість у хвилинах, до якої штраф не нараховується. */
    public static final int COMFORTABLE_MINUTES = 150;

    /** Штраф за кожну хвилину понад {@value #COMFORTABLE_MINUTES}. */
    public static final double PENALTY_PER_MINUTE = 0.1;

    /**
     * Створює художній фільм; спільні поля перевіряє базовий клас.
     *
     * @param title    назва
     * @param director режисер
     * @param year     рік випуску
     * @param minutes  тривалість у хвилинах
     * @param rating   рейтинг від 0 до 10
     * @throws IllegalArgumentException якщо значення недопустимі
     */
    public FeatureFilm(String title, String director, int year, int minutes, double rating) {
        super(title, director, year, minutes, rating, MovieKind.FEATURE);
    }

    /** Обчислює бал художнього фільму: довгі стрічки отримують штраф. */
    @Override
    public double recommendationScore() {
        int extraMinutes = Math.max(0, getMinutes() - COMFORTABLE_MINUTES);
        return getRating() * RATING_WEIGHT - extraMinutes * PENALTY_PER_MINUTE;
    }
}
