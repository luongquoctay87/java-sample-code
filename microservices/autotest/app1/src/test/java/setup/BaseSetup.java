package setup;

import helpers.TestListener;
import io.netty.util.internal.StringUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public class BaseSetup {

    public static WebDriver driver;

    public static WebDriver getDriver() {
        return driver;
    }

    public WebDriver setupDriver() {
        String browserType = System.getProperty("profileId");
        if (StringUtil.isNullOrEmpty(browserType)) {
            browserType = "chrome";
        }
        switch (browserType) {
            case "chrome" -> driver = initChromeDriver();
            case "firefox" -> driver = initFirefoxDriver();
            case "edge" -> driver = initEdgeDriver();
            default -> {
                System.out.println("Browser: " + browserType + " is invalid, Launching Chrome as browser of choice...");
                driver = initChromeDriver();
            }
        }
        return driver;
    }

    private void setDriver(String browserType, String appURL) {
        if (StringUtil.isNullOrEmpty(browserType)) {
            browserType = "chrome";
        }
        switch (browserType) {
            case "chrome" -> {
                driver = initChromeDriver();
                driver.navigate().to(appURL);
            }
            case "firefox" -> {
                driver = initFirefoxDriver();
                driver.navigate().to(appURL);
            }
            case "edge" -> driver = initEdgeDriver();
            default -> {
                System.out.println("Browser: " + browserType + " is invalid, Launching Chrome as browser of choice...");
                driver = initChromeDriver();
            }
        }
    }

    private WebDriver initChromeDriver() {
        System.out.println("Launching Chrome browser...");
        return new ChromeDriver();
    }

    private WebDriver initEdgeDriver() {
        System.out.println("Launching Edge browser...");
        return new EdgeDriver();
    }

    private WebDriver initFirefoxDriver() {
        System.out.println("Launching Firefox browser...");
        return new FirefoxDriver();
    }

    @BeforeClass
    public void setupBrowser() {
        driver = setupDriver();
    }

    @AfterClass
    public void closeBrowser() {
        driver.quit();
    }

}
