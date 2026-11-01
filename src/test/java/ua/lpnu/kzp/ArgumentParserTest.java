package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Перевіряє розбір аргументів командного рядка. */
class ArgumentParserTest {

    @Test
    void noArgumentsGiveDefaults() {
        CliOptions options = ArgumentParser.parse(new String[] {});

        assertFalse(options.help());
        assertFalse(options.version());
        assertEquals(Path.of("data", "input.csv"), options.input());
        assertEquals(Path.of("out", "report.txt"), options.output());
    }

    @Test
    void helpAndVersionFlagsAreRecognized() {
        assertTrue(ArgumentParser.parse(new String[] {"--help"}).help());
        assertTrue(ArgumentParser.parse(new String[] {"--version"}).version());
    }

    @Test
    void inputAndOutputValuesAreRead() {
        CliOptions options = ArgumentParser.parse(
                new String[] {"--input", "a.csv", "--output", "b.txt"});

        assertEquals(Path.of("a.csv"), options.input());
        assertEquals(Path.of("b.txt"), options.output());
    }

    @Test
    void unknownOptionIsRejected() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> ArgumentParser.parse(new String[] {"--bogus"}));

        assertTrue(error.getMessage().contains("--bogus"));
    }

    @Test
    void optionWithoutValueIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> ArgumentParser.parse(new String[] {"--input"}));
        assertThrows(IllegalArgumentException.class,
                () -> ArgumentParser.parse(new String[] {"--output"}));
    }
}
