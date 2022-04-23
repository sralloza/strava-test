package repositories;

import com.google.inject.Inject;
import config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import mappers.ActivityBuilder;
import models.Activity;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class StravaRepository {
    protected WebDriver driver;

    private final ConfigRepository configRepository;
    private final URLsRepository urlsRepository;
    private final ActivityBuilder activityBuilder;

    @Inject
    public StravaRepository(ConfigRepository configRepository,
                            URLsRepository urlsRepository,
                            ActivityBuilder activityBuilder) {
        this.configRepository = configRepository;
        this.urlsRepository = urlsRepository;
        this.activityBuilder = activityBuilder;
    }

    private void provisionDriver() {
        FirefoxOptions options = new FirefoxOptions();
//        options.addArguments("start-maximized");
        if (configRepository.getBoolean("general.headless")) {
            options.setHeadless(true);
        }

        driver = new FirefoxDriver(options);
    }

    public void login() {
        log.info("Logging in to Strava");

        provisionDriver();
        var username = configRepository.getString("strava.credentials.username");
        var password = configRepository.getString("strava.credentials.password");
        driver.get(urlsRepository.getLoginURL());
        driver.findElement(By.id("email")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);

        driver.findElement(By.id("login-button")).click();
        waitPageLoads();
        System.out.println(driver.getCurrentUrl());
    }

    public void logout() {
//        driver.get(getLogoutURL());
        driver.close();
    }

    protected void scrollToElement(WebElement element) {
        runJavascript("arguments[0].scrollIntoView(true);", element);
        waitPageLoads();
    }

    protected void waitPageLoads() {
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                webDriver -> runJavascript("return document.readyState").equals("complete"));
    }

    protected Object runJavascript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    public List<Activity> getActivities() {
        List<WebElement> activities = driver.findElements(By.className("react-feed-entry"));
        return activities.stream()
                .map(activityBuilder::buildActivities)
                .flatMap(Collection::stream)
                .peek(a -> log.info("Parsed activity: {}", a))
                .collect(Collectors.toList());
    }

    public void giveKudo(Activity activity) {
        log.info("Giving kudo to {}", activity);
        scrollToElement(activity.getKudoButton());
        activity.getKudoButton().click();
        waitPageLoads();
    }
}
