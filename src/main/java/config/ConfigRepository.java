package config;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import org.openqa.selenium.By;

public class ConfigRepository {
    private final Config config;

    @Inject
    private ConfigRepository(Config config) {
        this.config = config;
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public By getCssSelector(String variable) {
        return By.cssSelector(config.getString("strava.cssSelectors." + variable));
    }

    public String getTitle(String variable) {
        return config.getString("strava.title." + variable);
    }
}