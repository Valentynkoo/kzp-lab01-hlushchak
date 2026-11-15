package ua.lpnu.kzp;

import java.util.Objects;

/**
 * Вид фільму: фіксована множина значень із кодом для CSV та підписом для звіту.
 *
 * <p>Кожна константа сама створює фільм свого підтипу, тому в коді
 * завантаження немає розгалуження {@code if}/{@code switch} за видом.
 * Значення enum порівнюють через {@code ==}: кожна константа існує в єдиному
 * екземплярі.</p>
 */
public enum MovieKind {

    /** Художній (ігровий) фільм. */
    FEATURE("feature", "художній") {
        @Override
        public Movie create(String title, String director, int year, int minutes, double rating) {
            return new FeatureFilm(title, director, year, minutes, rating);
        }
    },

    /** Документальний фільм. */
    DOCUMENTARY("documentary", "документальний") {
        @Override
        public Movie create(String title, String director, int year, int minutes, double rating) {
            return new Documentary(title, director, year, minutes, rating);
        }
    };

    private final String code;
    private final String label;

    /** Конструктор викликає JVM один раз для кожної константи. */
    MovieKind(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /** Повертає код виду в CSV (англійською, малими літерами). */
    public String code() {
        return code;
    }

    /** Повертає український підпис виду для звіту. */
    public String label() {
        return label;
    }

    /**
     * Створює фільм підтипу, що відповідає цьому виду.
     *
     * @param title    назва
     * @param director режисер
     * @param year     рік випуску
     * @param minutes  тривалість у хвилинах
     * @param rating   рейтинг від 0 до 10
     * @return фільм відповідного підтипу
     * @throws IllegalArgumentException якщо значення недопустимі
     */
    public abstract Movie create(String title, String director, int year, int minutes,
                                 double rating);

    /**
     * Знаходить вид за кодом із CSV; регістр і пробіли по краях не враховуються.
     *
     * @param code код виду, наприклад {@code documentary}
     * @return відповідна константа
     * @throws NullPointerException     якщо код дорівнює null
     * @throws IllegalArgumentException якщо такого виду немає
     */
    public static MovieKind fromCode(String code) {
        Objects.requireNonNull(code, "Код виду не може бути null");
        String normalized = code.trim();
        for (MovieKind kind : values()) {
            if (kind.code.equalsIgnoreCase(normalized)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("невідомий вид фільму: " + normalized);
    }
}
