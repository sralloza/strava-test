package services;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import models.Activity;
import repositories.StravaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class StravaService {
    private final StravaRepository stravaRepository;

    @Inject
    public StravaService(StravaRepository stravaRepository) {
        this.stravaRepository = stravaRepository;
    }

    public void start() {
        log.info("Starting StravaService");
        stravaRepository.login();
        List<Activity> kudolessActivities = stravaRepository.getActivities().stream()
                .filter(activity -> activity.getDistance() > 1000)
                .filter(activity -> !activity.isHasKudo())
                .collect(Collectors.toList());

        log.info("Found {} kudoless activities", kudolessActivities.size());
        kudolessActivities.forEach(stravaRepository::giveKudo);
        stravaRepository.logout();
    }
}
