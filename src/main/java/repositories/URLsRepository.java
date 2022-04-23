package repositories;

import com.google.inject.Inject;
import config.ConfigRepository;

public class URLsRepository {
    private final String baseURL;

    @Inject
    public URLsRepository(ConfigRepository config) {
        baseURL = config.getString("strava.web.baseURL");
    }

    public String getLoginURL() {
        return baseURL + "/login";
    }
}
