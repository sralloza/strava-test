package utils;

import com.google.inject.Inject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeUtils {
    private final TimeProvider timeProvider;

    @Inject
    public TimeUtils(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public Duration parseDuration(String duration) {
        duration = duration.replace(" ", "")
                .replace("min", "m")
                .toUpperCase();
        return Duration.parse("PT" + duration);
    }

    public LocalDateTime parseDateTime(String s) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy",
                Locale.forLanguageTag("es-ES"));

        s = s.replace("de ", "").replace("a las ", "").toLowerCase();
        if (s.contains("ayer")) {
            s = s.replace("ayer", timeProvider.getYesterdayDate().format(timeFormatter));
        }

        if (s.contains("hoy")) {
            s = s.replace("hoy", timeProvider.getTodayDate().format(timeFormatter));
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy H:mm",
                Locale.forLanguageTag("es-ES"));
        return LocalDateTime.parse(s, dateTimeFormatter);
    }

}
