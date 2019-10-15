package runner;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.qameta.allure.Allure;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.MainPage;
import util.Wrapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static util.SizeReducer.resize;
import static util.Wrapper.sleep;

@RunWith(Cucumber.class)

@CucumberOptions(
        strict = true,
        features = {"src/test/resources/features/"},
        tags = "@all",
        plugin = {"io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm"},
        glue = {"steps", "hooks"})
public class RunnerTest {

    private static Logger logger = LoggerFactory.getLogger(RunnerTest.class);
    public static IOSDriver<IOSElement> driver;
    public final static String UDID = System.getProperty("udid");
    //Для возможности записи видео выполнения тестов необходимо установить ffmpeg (brew install ffmpeg)
    public final static boolean VIDEO_RECORDING = Boolean.parseBoolean(System.getProperty("video"));
    public static Wrapper wrapper;
    public static MainPage mainPage;

    @BeforeClass
    public static void setUpDriver() throws MalformedURLException {

        File app = new File("src/test/resources/ipa/roman-app.app");
        DesiredCapabilities ipadCapabilities = new DesiredCapabilities();
        ipadCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "12.3.1");
        ipadCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.IOS);
        ipadCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"iPad");
        ipadCapabilities.setCapability("udid","c680624a6334d9980191ce3e61b08e75567a567e");
        ipadCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        ipadCapabilities.setCapability("appium-version", "1.15.0");
        ipadCapabilities.setCapability("autoAcceptAlerts",false);
        ipadCapabilities.setCapability("noReset","true");
        //Параметр удаляет wda и устанавливает новый при каждом запуске тестов
        ipadCapabilities.setCapability("useNewWDA","true");
        ipadCapabilities.setCapability(MobileCapabilityType.APP,app.getAbsolutePath());

        driver = new IOSDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), ipadCapabilities);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        initPage();
    }

    public static void initPage() {
        wrapper = new Wrapper(driver);
    }

    /**
     * Сделать скриншот экрана
     *
     * @param percentage изменить размер изображения, 1.0 исходный размер
     */
    public static void makeScreenshot(double percentage) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        byte[] fileContent = new byte[0];
        try {
            fileContent = toByteArrayAutoClosable(resize(scrFile, percentage));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        Allure.getLifecycle().addAttachment("Скриншот", "image/png", "png", fileContent);
    }

    private static byte[] toByteArrayAutoClosable(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        }
    }
}
