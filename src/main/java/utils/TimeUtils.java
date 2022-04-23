package utils;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
public class TimeUtils {
    private final TimeProvider timeProvider;
    private final ConfigRepository configRepository;

    @Inject
    public TimeUtils(TimeProvider timeProvider, ConfigRepository configRepository) {
        this.timeProvider = timeProvider;
        this.configRepository = configRepository;
    }

    public Duration parseDuration(String duration) {
        duration = duration.replace(" ", "")
                .replace("min", "m")
                .toUpperCase();
        return Duration.parse("PT" + duration);
    }

    private Locale getLocale() {
        String locale = configRepository.getBoolean("general.spanishLocale") ? "es-ES" : "en-US";
        log.debug("Using locale: {}", locale);
        return Locale.forLanguageTag(locale);
    }

    private DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern("dd MMMM yyyy", getLocale());
    }

    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("d MMMM yyyy H:mm", getLocale());
    }

    public LocalDateTime parseDateTime(String s) {
        DateTimeFormatter dateFormatter = getDateFormatter();

        s = s.replace("de ", "").replace("a las ", "").toLowerCase();
        if (s.contains("ayer")) {
            s = s.replace("ayer", timeProvider.getYesterdayDate().format(dateFormatter));
        }
        if (s.contains("yesterday")) {
            s = s.replace("yesterday", timeProvider.getYesterdayDate().format(dateFormatter));
        }

        if (s.contains("hoy")) {
            s = s.replace("hoy", timeProvider.getTodayDate().format(dateFormatter));
        }
        if (s.contains("today")) {
            s = s.replace("today", timeProvider.getTodayDate().format(dateFormatter));
        }

        DateTimeFormatter dateTimeFormatter = getDateTimeFormatter();
        return LocalDateTime.parse(s, dateTimeFormatter);
    }

}
